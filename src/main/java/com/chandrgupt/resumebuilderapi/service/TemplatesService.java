package com.chandrgupt.resumebuilderapi.service;

import com.chandrgupt.resumebuilderapi.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chandrgupt.resumebuilderapi.util.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplatesService {

    private final AuthService authService;

    public Map<String,Object> getTemplates(Object principal){

        AuthResponse authResponse=authService.getProfile(principal);



        List<String> availableTemplates;

        Boolean isPremium=PREMIUM.equalsIgnoreCase(authResponse.getSubscriptionPlan());

        if(isPremium){
            availableTemplates = List.of("01","02","03");
        }else{
            availableTemplates= List.of("01");
        }

        Map<String,Object>restriction = new HashMap<>();
        restriction.put("availableTemplates",availableTemplates);
        restriction.put("allTemplates",List.of("01","02","03"));
        restriction.put("subscriptionPlan",authResponse.getSubscriptionPlan());
        restriction.put("isPremium",isPremium);

        return restriction;
    }

}
