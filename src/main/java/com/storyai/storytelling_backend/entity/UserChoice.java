package com.storyai.storytelling_backend.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "user_choices")
public class UserChoice {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private StorySession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private StoryChapter chapter;

    @Column(name = "choice_text", columnDefinition = "TEXT", nullable = false)
    private String choicesText;

    @Column(name = "choice_order")
    private Integer choiceOrder = 1;

    @CreationTimestamp
    private LocalDateTime timestamp;

    // Constructors
    public UserChoice() {
    }

    public UserChoice(StorySession session,
                      StoryChapter chapter,
                      String choiceText) {
        this.session = session;
        this.chapter = chapter;
        this.choicesText = choiceText;
    }
}
