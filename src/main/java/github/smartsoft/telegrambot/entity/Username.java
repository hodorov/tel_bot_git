package github.smartsoft.telegrambot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "github_nickname")
public class Username implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    @Column(name = "is_teamlead")
    private boolean isTeamlead = false;

    @OneToMany(mappedBy = "telegramId", fetch = FetchType.EAGER)
    private Collection<CreateRequest> createRepositories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public boolean isTeamlead() {
        return isTeamlead;
    }

    public void setTeamlead(boolean teamlead) {
        isTeamlead = teamlead;
    }

    public Collection<CreateRequest> getCreateRepositories() {
        return createRepositories;
    }

    public void setCreateRepositories(Collection<CreateRequest> createRepositories) {
        this.createRepositories = createRepositories;
    }
}
