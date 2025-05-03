package aiss.bitbucketminer.etl;

import aiss.bitbucketminer.model.bitbucket.BitBucketComment;
import aiss.bitbucketminer.model.bitbucket.BitBucketProject;
import aiss.bitbucketminer.model.bitbucket.BitbucketCommit;
import aiss.bitbucketminer.model.gitminer.*;

import aiss.bitbucketminer.model.bitbucket.BitBucketIssue;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

    public static Commit toGitMinerCommit(BitbucketCommit bitbucketCommit) {
        Commit commit = new Commit();

        // ID del commit
        commit.setId(bitbucketCommit.getHash());

        // Título (usamos la primera línea del mensaje como título)
        String[] messageLines = bitbucketCommit.getMessage().split("\n", 2);
        commit.setTitle(messageLines[0]);

        // Mensaje completo
        commit.setMessage(bitbucketCommit.getMessage());

        // Autor
        String rawAuthor = bitbucketCommit.getAuthor() != null ? bitbucketCommit.getAuthor().getRaw() : "Unknown";
        String[] parts = rawAuthor.split("<");
        String authorName = parts[0].trim();
        String authorEmail = (parts.length > 1) ? parts[1].replace(">", "").trim() : "";

        commit.setAuthorName(authorName);
        commit.setAuthorEmail(authorEmail);
        commit.setCommitterName(authorName);    // Bitbucket no distingue, así que usamos el mismo
        commit.setCommitterEmail(authorEmail);

        // Fechas
        String date = bitbucketCommit.getDate();
        commit.setAuthoredDate(date);
        commit.setCommittedDate(date);

        // URL web del commit
        String webUrl = bitbucketCommit.getLinks().get("html").getHref();
        commit.setWebUrl(webUrl);

        return commit;
    }

    public static Issue toGitMinerIssue(BitBucketIssue bitbucketIssue) {
        Issue issue = new Issue();

        // ID: Bitbucket no nos da un UUID, así que podemos usar null y dejar que GitMiner genere uno, o usar el refId como fallback
        issue.setId(null);  // o UUID.randomUUID().toString();

        // refId: usamos el ID numérico de Bitbucket
        issue.setRefId(bitbucketIssue.getId() != null ? String.valueOf(bitbucketIssue.getId()) : null);

        issue.setTitle(bitbucketIssue.getTitle());

        String description = (bitbucketIssue.getContent() != null)
                ? bitbucketIssue.getContent().getRaw()
                : "";
        issue.setDescription(description);

        issue.setState(bitbucketIssue.getState());
        issue.setCreatedAt(bitbucketIssue.getCreated_on());
        issue.setUpdatedAt(bitbucketIssue.getUpdated_on());
        issue.setClosedAt(null);  // Bitbucket no devuelve esto

        // Labels: combinamos 'kind' y 'priority' en una lista simple
        List<String> labels = new ArrayList<>();
        if (bitbucketIssue.getKind() != null) labels.add(bitbucketIssue.getKind());
        if (bitbucketIssue.getPriority() != null) labels.add(bitbucketIssue.getPriority());
        issue.setLabels(labels);

        // Author: creamos un User básico
        User author = new User();
        if (bitbucketIssue.getReporter() != null) {
            // En Bitbucket a veces 'display_name' es el nombre completo
            author.setName(bitbucketIssue.getReporter().getDisplay_name());

            // No tenemos 'username' claro en Bitbucket Issues (puedes reutilizar el display_name o poner algo genérico)
            author.setUsername(bitbucketIssue.getReporter().getDisplay_name());

            // Avatar y webUrl: si no tienes en la respuesta del issue, puedes dejar null o buscar en la API de users (opcional)
            author.setAvatarUrl(null);
            author.setWebUrl(null);

            // ID: Bitbucket no da un ID directo del user en Issues; si no lo tienes, puedes generar uno o dejar null
            author.setId(null);
        } else {
            author.setName("Unknown");
            author.setUsername("unknown");
            author.setAvatarUrl(null);
            author.setWebUrl(null);
            author.setId(null);
        }
        issue.setAuthor(author);


        // Assignee: no tenemos en Bitbucket Issue, dejamos null
        issue.setAssignee(null);

        // Upvotes y downvotes: Bitbucket no lo da, dejamos en 0 o null
        issue.setUpvotes(0);
        issue.setDownvotes(0);

        // webUrl
        String webUrl = (bitbucketIssue.getLinks() != null
                && bitbucketIssue.getLinks().getHtml() != null)
                ? bitbucketIssue.getLinks().getHtml().getHref()
                : "";
        issue.setWebUrl(webUrl);

        // Comments: por ahora vacío (se pueden agregar luego si haces otra llamada para comentarios)
        issue.setComments(new ArrayList<>());

        return issue;
    }

    public static Comment toGitMinerComment(BitBucketComment bitbucketComment) {
        Comment comment = new Comment();

        // ID (Bitbucket nos da Integer, GitMiner espera String)
        comment.setId(bitbucketComment.getId() != null ? String.valueOf(bitbucketComment.getId()) : null);

        // Body (texto del comentario)
        String body = (bitbucketComment.getContent() != null)
                ? bitbucketComment.getContent().getRaw()
                : "";
        comment.setBody(body);

        // Fechas
        comment.setCreatedAt(bitbucketComment.getCreated_on() != null ? bitbucketComment.getCreated_on() : "");
        comment.setUpdatedAt(bitbucketComment.getUpdated_on());

        // Author (mapeamos User de Bitbucket a User de GitMiner)
        User author = new User();
        if (bitbucketComment.getUser() != null) {
            author.setName(bitbucketComment.getUser().getDisplay_name());
            author.setUsername(bitbucketComment.getUser().getDisplay_name());
            author.setId(bitbucketComment.getUser().getUuid());

            // Opcionales, porque en comentarios no siempre tienes avatar/webUrl
            author.setAvatarUrl(null);
            author.setWebUrl(null);
        } else {
            author.setName("Unknown");
            author.setUsername("unknown");
            author.setId(null);
            author.setAvatarUrl(null);
            author.setWebUrl(null);
        }
        comment.setAuthor(author);

        return comment;
    }

    public static Project toGitMinerProject(BitBucketProject bitbucketProject) {
        Project project = new Project();

        // ID (Bitbucket nos da uuid)
        project.setId(bitbucketProject.getUuid());

        // Name
        project.setName(bitbucketProject.getName());

        // Web URL
        String webUrl = (bitbucketProject.getLinks() != null
                && bitbucketProject.getLinks().getHtml() != null)
                ? bitbucketProject.getLinks().getHtml().getHref()
                : "";
        project.setWebUrl(webUrl);

        // Inicializamos las listas vacías (ya lo hace el constructor pero por claridad)
        project.setCommits(new ArrayList<>());
        project.setIssues(new ArrayList<>());

        return project;
    }



}
