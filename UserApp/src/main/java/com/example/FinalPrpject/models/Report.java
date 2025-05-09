package com.example.FinalPrpject.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who made the report
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // User who was reported
    @ManyToOne
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Column(nullable = false)
    private String reason;

    private LocalDateTime timestamp = LocalDateTime.now();

    public Report() {}

    public Report(User reporter, User reported, String reason) {
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
    }

    // Getters and setters

    public Long getId() { return id; }

    public User getReporter() { return reporter; }

    public void setReporter(User reporter) { this.reporter = reporter; }

    public User getReported() { return reported; }

    public void setReported(User reported) { this.reported = reported; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
