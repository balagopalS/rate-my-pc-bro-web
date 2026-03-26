package com.ratemypcbro.dto;

public class GeneralVerdict {
    private String rating;
    private String verdict;
    private String roast;

    public GeneralVerdict() {}

    public GeneralVerdict(String rating, String verdict, String roast) {
        this.rating = rating;
        this.verdict = verdict;
        this.roast = roast;
    }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getRoast() { return roast; }
    public void setRoast(String roast) { this.roast = roast; }

    @Override
    public String toString() {
        return "GeneralVerdict{" +
                "rating='" + rating + '\'' +
                ", verdict='" + verdict + '\'' +
                ", roast='" + roast + '\'' +
                '}';
    }
}
