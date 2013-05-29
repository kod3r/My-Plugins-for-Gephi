/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.clementlevallois.classes;

/**
 *
 * @author C. Levallois
 */
public class TempMetrics {

    private int inDegree;
    private int outDegree;
    private int degree;
    private int ratioInToOutDegree;
    private int community;
    private int degreeWithDifferentCommunities;
    private int inDegreeWithDifferentCommunities;
    private int outDegreeWithDifferentCommunities;
    private boolean isHighestDegreeWithDifferentCommunities;
    private boolean isHighestInDegreeWithDifferentCommunities;
    private boolean isHighestOutDegreeWithDifferentCommunities;
    private String role;
    private int nbDiffCommunitiesOut;
    private int nbDiffCommunitiesIn;
    private int nbDiffCommunities;

    public TempMetrics() {
        isHighestDegreeWithDifferentCommunities = false;
        isHighestInDegreeWithDifferentCommunities = false;
        isHighestOutDegreeWithDifferentCommunities = false;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    public int getRatioInToOutDegree() {
        return ratioInToOutDegree;
    }

    public void setRatioInToOutDegree(int ratioInToOutDegree) {
        this.ratioInToOutDegree = ratioInToOutDegree;
    }

    public int getCommunity() {
        return community;
    }

    public void setCommunity(int community) {
        this.community = community;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getDegreeWithDifferentCommunities() {
        return degreeWithDifferentCommunities;
    }

    public void setDegreeWithDifferentCommunities(int degreeWithDifferentCommunities) {
        this.degreeWithDifferentCommunities = degreeWithDifferentCommunities;
    }

    public int getInDegreeWithDifferentCommunities() {
        return inDegreeWithDifferentCommunities;
    }

    public void setInDegreeWithDifferentCommunities(int inDegreeWithDifferentCommunities) {
        this.inDegreeWithDifferentCommunities = inDegreeWithDifferentCommunities;
    }

    public int getOutDegreeWithDifferentCommunities() {
        return outDegreeWithDifferentCommunities;
    }

    public void setOutDegreeWithDifferentCommunities(int outDegreeWithDifferentCommunities) {
        this.outDegreeWithDifferentCommunities = outDegreeWithDifferentCommunities;
    }

    public boolean isIsHighestDegreeWithDifferentCommunities() {
        return isHighestDegreeWithDifferentCommunities;
    }

    public void setIsHighestDegreeWithDifferentCommunities(boolean isHighestDegreeWithDifferentCommunities) {
        this.isHighestDegreeWithDifferentCommunities = isHighestDegreeWithDifferentCommunities;
    }

    public boolean isIsHighestInDegreeWithDifferentCommunities() {
        return isHighestInDegreeWithDifferentCommunities;
    }

    public void setIsHighestInDegreeWithDifferentCommunities(boolean isHighestInDegreeWithDifferentCommunities) {
        this.isHighestInDegreeWithDifferentCommunities = isHighestInDegreeWithDifferentCommunities;
    }

    public boolean isIsHighestOutDegreeWithDifferentCommunities() {
        return isHighestOutDegreeWithDifferentCommunities;
    }

    public void setIsHighestOutDegreeWithDifferentCommunities(boolean isHighestOutDegreeWithDifferentCommunities) {
        this.isHighestOutDegreeWithDifferentCommunities = isHighestOutDegreeWithDifferentCommunities;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNbDiffCommunitiesOut() {
        return nbDiffCommunitiesOut;
    }

    public void setNbDiffCommunitiesOut(int nbDiffCommunitiesOut) {
        this.nbDiffCommunitiesOut = nbDiffCommunitiesOut;
    }

    public int getNbDiffCommunitiesIn() {
        return nbDiffCommunitiesIn;
    }

    public void setNbDiffCommunitiesIn(int nbDiffCommunitiesIn) {
        this.nbDiffCommunitiesIn = nbDiffCommunitiesIn;
    }

    public int getNbDiffCommunities() {
        return nbDiffCommunities;
    }

    public void setNbDiffCommunities(int nbDiffCommunities) {
        this.nbDiffCommunities = nbDiffCommunities;
    }
}
