package com.storyai.storytelling_backend.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.List;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story_sessions")
public class StorySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "session_data", columnDefinition = "JSONB")
    private String sessionData = "{}";

    @Column(name = "current_chapter")
    private Integer currentChapter = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_played")
    private LocalDateTime lastPlayed;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserChoice> choices;

    // Constructors
    public StorySession() {}

    public StorySession(User user, Story story) {
        this.user = user;
        this.story = story;
    }

}
