package com.storyai.storytelling_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "story_chapters")
public class StoryChapter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "ai_generated")
    private Boolean aiGenerated = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructor
    // public StoryChapter() {}

    public StoryChapter(Story story, Integer chapterNumber, String title, String content) {
        this.story = story;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
    }
    

}
