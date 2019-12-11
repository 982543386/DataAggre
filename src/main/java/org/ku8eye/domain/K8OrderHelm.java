package org.ku8eye.domain;

import java.util.Date;

public class K8OrderHelm {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.id
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.k8order_id
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String k8orderId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.helm_name
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String helmName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.res
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.res1
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private Integer res1;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.k8order_type
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String k8OrderType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.res3
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private Boolean res3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.source
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String source;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.version
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String version;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.charts
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String charts;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.settings
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private String settings;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.time
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private Date time;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column k8order_helm.status_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    private Integer statusHelm;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.id
     *
     * @return the value of k8order_helm.id
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.id
     *
     * @param id the value for k8order_helm.id
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.k8order_id
     *
     * @return the value of k8order_helm.k8order_id
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getK8orderId() {
        return k8orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.k8order_id
     *
     * @param k8orderId the value for k8order_helm.k8order_id
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setK8orderId(String k8orderId) {
        this.k8orderId = k8orderId == null ? null : k8orderId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.helm_name
     *
     * @return the value of k8order_helm.helm_name
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getHelmName() {
        return helmName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.helm_name
     *
     * @param helmName the value for k8order_helm.helm_name
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setHelmName(String helmName) {
        this.helmName = helmName == null ? null : helmName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.res
     *
     * @return the value of k8order_helm.res
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.res
     *
     * @param res the value for k8order_helm.res
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.res1
     *
     * @return the value of k8order_helm.res1
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public Integer getRes1() {
        return res1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.res1
     *
     * @param res1 the value for k8order_helm.res1
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setRes1(Integer res1) {
        this.res1 = res1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.k8order_type
     *
     * @return the value of k8order_helm.res2
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getK8OrderType() {
        return k8OrderType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.res2
     *
     * @param res2 the value for k8order_helm.res2
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setK8OrderType(String k8OrderType) {
        this.k8OrderType = k8OrderType == null ? null : k8OrderType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.res3
     *
     * @return the value of k8order_helm.res3
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public Boolean getRes3() {
        return res3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.res3
     *
     * @param res3 the value for k8order_helm.res3
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setRes3(Boolean res3) {
        this.res3 = res3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.source
     *
     * @return the value of k8order_helm.source
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getSource() {
        return source;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.source
     *
     * @param source the value for k8order_helm.source
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.version
     *
     * @return the value of k8order_helm.version
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.version
     *
     * @param version the value for k8order_helm.version
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.charts
     *
     * @return the value of k8order_helm.charts
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getCharts() {
        return charts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.charts
     *
     * @param charts the value for k8order_helm.charts
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setCharts(String charts) {
        this.charts = charts == null ? null : charts.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.settings
     *
     * @return the value of k8order_helm.settings
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public String getSettings() {
        return settings;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.settings
     *
     * @param settings the value for k8order_helm.settings
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setSettings(String settings) {
        this.settings = settings == null ? null : settings.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.time
     *
     * @return the value of k8order_helm.time
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public Date getTime() {
        return time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.time
     *
     * @param time the value for k8order_helm.time
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column k8order_helm.status_helm
     *
     * @return the value of k8order_helm.status_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public Integer getStatusHelm() {
        return statusHelm;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column k8order_helm.status_helm
     *
     * @param statusHelm the value for k8order_helm.status_helm
     *
     * @mbg.generated Fri Aug 18 14:32:05 CST 2017
     */
    public void setStatusHelm(Integer statusHelm) {
        this.statusHelm = statusHelm;
    }
}