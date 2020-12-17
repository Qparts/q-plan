package q.rest.plan.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="pln_plan")
public class PublicPlan {

    @Id
    private int id;
    private double price;
    private String name;
    private String nameAr;
    @JsonIgnore
    private char status;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    @Where(clause = "status = 'A'")
    private List<PublicPlanDuration> planDurations;
    @JsonIgnore
    private int generalRoleId;

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

    public List<PublicPlanDuration> getPlanDurations() {
        return planDurations;
    }

    public void setPlanDurations(List<PublicPlanDuration> planDurations) {
        this.planDurations = planDurations;
    }

    public int getGeneralRoleId() {
        return generalRoleId;
    }

    public void setGeneralRoleId(int generalRoleId) {
        this.generalRoleId = generalRoleId;
    }
}
