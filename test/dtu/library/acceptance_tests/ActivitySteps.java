package dtu.library.acceptance_tests;

import app.Employee;
import app.OperationNotAllowedException;
import app.Model;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class ActivitySteps {
    private Model model;
    private ErrorMessageHolder errorMessage;

    public ActivitySteps(Model model, ErrorMessageHolder errorMessage) {
        this.model = model;
        this.errorMessage = errorMessage;
    }

    @When("the user adds an activity with name {string} to the project with ID {int}")
    public void userAddsActivityToProject(String name, int id) {
        try {
            model.addNewActivity(name, id);
        } catch (OperationNotAllowedException e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("project {int} will contain activity {string}")
    public void projectContainsActivity(int id, String name) {
        assertTrue(model.getProject(id).hasActivity(name));
    }

    @Given("project {int} contains activity {string}")
    public void projectAlreadyContainsActivity(int id, String name) {
        Employee temp = model.getUser();
        model.setUser(model.getProject(id).getPm());
        userAddsActivityToProject(name, id);
        model.setUser(temp);
    }

    @When("the user sets the start date as {int} and end date as {int} of {string} in project {int}")
    public void userSetsDatesOfProjectActivity(int start, int end, String name, int id) {
        try {
            model.setDates(id, name, start, end);
        } catch (OperationNotAllowedException e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the activity with name {string} in project {int} has start date {int} and end date {int}")
    public void projectActivityHasDates(String name, int id, int start, int end) {
        assertEquals(model.getProject(id).getDates(name)[0], start);
        assertEquals(model.getProject(id).getDates(name)[1], end);
    }

    @When("the user sets the name of {string} from project {int} to {string}")
    public void userSetsNameOfProjectActivity(String name, int id, String newname) {
        try {
            model.setName(name, id, newname);
        } catch (OperationNotAllowedException e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project with ID {int} does not contain an activity with name {string}")
    public void projectDoesNotContainActivity(int id, String name) {
        assertFalse(model.getProject(id).hasActivity(name));
    }

    @When("the user registers absence from date {int} to date {int}")
    public void userRegistersAbsence(int start, int end) {
        model.registerAbsence(start, end);
    }

    @Then("the user is absent for date {int}")
    public void userIsAbsent(int date) {
        assertTrue(model.getAbsence().getWorkTime().get(model.getUser()).get(date) == 8);
    }

    @Then("the user is absent for date {int} till date {int}")
    public void userIsAbsentForMultipleDates(int start, int end) {

        Calendar current = new GregorianCalendar(start/10000, (start%10000)/100, (start%100));
        Calendar last    = new GregorianCalendar(end/10000, (end%10000)/100, (end%100));
        int date;

        while(!current.after(last)) {
            date = current.get(Calendar.YEAR) * 10000 + current.get(Calendar.MONTH)  * 100 + current.get(Calendar.DATE) ;
            current.set(Calendar.DATE,current.get(Calendar.DATE) + 1);
            userIsAbsent(date);
        }
    }

    @When("the user sets worktime of {float} hours to {string} on date {int} on project {int}")
    public void userSetsWorktimeOfActivity(float time, String name, int date, int id) {
        model.setWorkTime(date, time, id, name);
    }

    @When("the user sets the expected worktime of {string} from project {int} to {float}")
    public void userSetsExpectedWorktimeOfActivity(String name, int id, float time) {
        try {
            model.setExpectedWT(id, name, time);
        } catch (OperationNotAllowedException e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("{string} on project {int} on date {int} has {float} hours from user")
    public void activityHasWorktimeFromUser(String name, int id, int date, float time) {
        assertTrue(model.getProject(id).getActivity(name).getWorkTime().get(model.getUser()).get(date) == time);
    }

    @Then("{string} of project {int} has expected worktime {float}")
    public void activityHasExpectedWorktime(String name, int id, float time) {
        assertTrue(model.getProject(id).getActivity(name).getExpectedWorkTime() == time);
    }

}
