package aiss.githubminer.tranformer;

import aiss.githubminer.model.*;
import aiss.githubminer.model.gitminer.GMComment;
import aiss.githubminer.model.gitminer.GMCommit;
import aiss.githubminer.model.gitminer.GMIssue;
import aiss.githubminer.model.gitminer.GMUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static GMUser mapUser(User user) {
        if(user == null) return null;
        GMUser u = new GMUser();
        u.setId(String.valueOf(user.getId()));
        u.setUsername(user.getLogin());
        u.setName(user.getLogin());
        u.setAvatarUrl(user.getAvatarUrl());
        u.setWebUrl(user.getHtmlUrl());
        return u;
    }

    public static List<GMUser> mapUsers(List<User> users, String projectWebUrl) {
        List<GMUser> result = new ArrayList<>();
        for (User user : users) {
            result.add(mapUser(user));
        }
        return result;
    }
}
