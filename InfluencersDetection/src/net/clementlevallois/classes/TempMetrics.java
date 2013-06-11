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
    private int followers;
    private int ratioInToOutDegree;
    private int community;
    private int degreeWithDifferentCommunities;
    private int inDegreeWithDifferentCommunities;
    private int outDegreeWithDifferentCommunities;
    private String role;
    private int nbDiffCommunitiesOut;
    private int nbDiffCommunitiesIn;
    private int nbDiffCommunities;
    private float localEigenvectorCentrality;

    public TempMetrics() {
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

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public float getLocalEigenvectorCentrality() {
        return localEigenvectorCentrality;
    }

    public void setLocalEigenvectorCentrality(float localEigenvectorCentrality) {
        this.localEigenvectorCentrality = localEigenvectorCentrality;
    }
    
    
    
    
}
