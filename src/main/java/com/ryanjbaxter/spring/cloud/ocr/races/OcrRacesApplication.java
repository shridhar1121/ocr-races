package com.ryanjbaxter.spring.cloud.ocr.races;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.ArrayList;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
public class OcrRacesApplication implements CommandLineRunner {

    private static List<Race> races = new ArrayList<Race>();
    @Autowired
    private ParticipantsBean participantsBean;

    public static void main(String[] args) {

        SpringApplication.run(OcrRacesApplication.class, args);

    }

    @Override
    public void run(String... arg0) throws Exception {

        races.add(new Race("Spartan Beast", "123", "MA", "Boston"));

        races.add(new Race("Tough Mudder RI", "456", "RI", "Providence"));

    }

    @RequestMapping("/")
    public List<Race> getRaces() {

        return races;

    }

    @RequestMapping("/participants")
    public List<RaceWithParticipants> getRacesWithParticipants() {
        List<RaceWithParticipants> returnRaces = new ArrayList<RaceWithParticipants>();

        for (Race r : races) {
            returnRaces.add(new RaceWithParticipants(r, participantsBean.getParticipants(r.getId())));
        }
        return returnRaces;
        
    }
}

@Component
class ParticipantsBean {

    @Autowired
    private ParticipantsClient participantsClient;
    @HystrixCommand(fallbackMethod = "defaultParticipants")
    public List<Participant> getParticipants(String raceId) {
        return participantsClient.getParticipants(raceId);
    }
    public List<Participant> defaultParticipants(String raceId) {
        return new ArrayList<Participant>();
    }
}

class Race {

    private String name;

    private String id;

    private String state;

    private String city;

    public Race(String name, String id, String state, String city) {

        super();

        this.name = name;

        this.id = id;

        this.state = state;

        this.city = city;

    }

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public String getId() {

        return id;

    }

    public void setId(String id) {

        this.id = id;

    }

    public String getState() {

        return state;

    }

    public void setState(String state) {

        this.state = state;

    }

    public String getCity() {

        return city;

    }

    public void setCity(String city) {

        this.city = city;

    }

}

class Participant {

    private String firstName;

    private String lastName;

    private String homeState;

    private String shirtSize;

    private List<String> races;

    public Participant(String firstName, String lastName, String homeState,
            String shirtSize, List<String> races) {

        super();

        this.firstName = firstName;

        this.lastName = lastName;

        this.homeState = homeState;

        this.shirtSize = shirtSize;

        this.races = races;

    }

    public Participant() {
    }

    public String getFirstName() {

        return firstName;

    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;

    }

    public String getLastName() {

        return lastName;

    }

    public void setLastName(String lastName) {

        this.lastName = lastName;

    }

    public String getHomeState() {

        return homeState;

    }

    public void setHomeState(String homeState) {

        this.homeState = homeState;

    }

    public String getShirtSize() {

        return shirtSize;

    }

    public void setShirtSize(String shirtSize) {

        this.shirtSize = shirtSize;

    }

    public List<String> getRaces() {

        return races;

    }

    public void setRaces(List<String> races) {

        this.races = races;

    }

}

class RaceWithParticipants extends Race {

    private List<Participant> participants;

    public RaceWithParticipants(Race r, List<Participant> participants) {

        super(r.getName(), r.getId(), r.getState(), r.getCity());

        this.participants = participants;

    }

    public List<Participant> getParticipants() {

        return participants;

    }

    public void setParticipants(List<Participant> participants) {

        this.participants = participants;

    }

}

@FeignClient("participants")
interface ParticipantsClient {

    @RequestMapping(method = RequestMethod.GET, value = "/races/{raceId}")

    List<Participant> getParticipants(@PathVariable("raceId") String raceId);

}
