package q.rest.plan.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="pln_duration")
public class PlanDuration {
    @Id
    @SequenceGenerator(name = "pln_duration_id_seq_gen", sequenceName = "pln_duration_id_seq", initialValue=1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "pln_duration_id_seq_gen")
    private int id;
    private int calculationDays;
    private int actualDays;
    @Column(name="plan_id")
    private int planId;
    private boolean indefinite;
    private char status;
    private double discountPercentage;


    public boolean isIndefinite() {
        return indefinite;
    }

    public void setIndefinite(boolean indefinite) {
        this.indefinite = indefinite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCalculationDays() {
        return calculationDays;
    }

    public void setCalculationDays(int calculationDays) {
        this.calculationDays = calculationDays;
    }

    public int getActualDays() {
        return actualDays;
    }

    public void setActualDays(int actualDays) {
        this.actualDays = actualDays;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }
}
