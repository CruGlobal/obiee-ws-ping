package org.cru.obieewsping;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PingRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
