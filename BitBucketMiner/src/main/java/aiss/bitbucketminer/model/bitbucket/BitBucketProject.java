package aiss.bitbucketminer.model.bitbucket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BitBucketProject {

    private String uuid;
    private String name;
    private Links links;

    public static class Links {
        private Html html;

        public Html getHtml() { return html; }
        public void setHtml(Html html) { this.html = html; }
    }

    public static class Html {
        private String href;

        public String getHref() { return href; }
        public void setHref(String href) { this.href = href; }
    }

    // Getters & Setters

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Links getLinks() { return links; }
    public void setLinks(Links links) { this.links = links; }
}

