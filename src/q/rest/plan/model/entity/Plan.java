package q.rest.plan.model.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="pln_plan")
public class Plan {
    @Id
    @SequenceGenerator(name = "pln_plan_id_seq_gen", sequenceName = "pln_plan_id_seq", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "pln_plan_id_seq_gen")
    private int id;
    private double price;
    private String name;
    private String nameAr;
    private char status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private int createdBy;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    private List<PlanDuration> planDurations;
    private int generalRoleId;
    @Transient
    private List<PlanPromotion> promotions;


    public int getGeneralRoleId() {
        return generalRoleId;
    }

    public void setGeneralRoleId(int generalRoleId) {
        this.generalRoleId = generalRoleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public List<PlanDuration> getPlanDurations() {
        return planDurations;
    }

    public void setPlanDurations(List<PlanDuration> planDurations) {
        this.planDurations = planDurations;
    }

    public List<PlanPromotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PlanPromotion> promotions) {
        this.promotions = promotions;
    }
}
