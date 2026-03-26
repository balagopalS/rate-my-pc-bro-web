package com.ratemypcbro.dto;

public class SoftwareVerdict {
    private String software;
    private String score;
    private String verdict;
    private String performance_notes;

    public SoftwareVerdict() {}

    public SoftwareVerdict(String software, String score, String verdict, String performance_notes) {
        this.software = software;
        this.score = score;
        this.verdict = verdict;
        this.performance_notes = performance_notes;
    }

    public String getSoftware() { return software; }
    public void setSoftware(String software) { this.software = software; }

    public String getScore() { return score; }
    public void setScore(String score) { this.score = score; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getPerformance_notes() { return performance_notes; }
    public void setPerformance_notes(String performance_notes) { this.performance_notes = performance_notes; }

    @Override
    public String toString() {
        return "SoftwareVerdict{" +
                "software='" + software + '\'' +
                ", score='" + score + '\'' +
                ", verdict='" + verdict + '\'' +
                ", performance_notes='" + performance_notes + '\'' +
                '}';
    }
}
