package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.enums.PlanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    /**
     *
     * @param planId
     * @return
     */
    public Plan findByPlanId(int planId) {
        Optional<Plan> plan = planRepository.findById(planId);

        if(plan.isPresent())
            return plan.get();

        return null;
    }

    /**
     * Creates a BASIC or PRO Plan
     * @param planId
     * @return the created Plan
     * @throws IllegalArgumentException If plan id is not 1 or 2
     */
    @Transactional
    public Plan createPlan(int planId) {

        Plan plan = null;
        if(planId == 1)
            planRepository.save(new Plan(PlanEnum.BASIC));
        else if(planId == 2)
            planRepository.save(new Plan(PlanEnum.PRO));
        else
            throw new IllegalArgumentException(String.format("Plan id %s not recognized", planId));

        return plan;
    }
}
