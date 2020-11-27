package com.airport_management.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import com.airport_management.controller.FlightController;
import com.airport_management.controller.PlaneController;
import com.airport_management.controller.SearchController;
import com.airport_management.model.Plane;


@RunWith(GUITestRunner.class)
public class AirportSwingViewPlanePanelTest extends AssertJSwingJUnitTestCase{
	
	private FrameFixture window;
	private AirportSwingView airportSwingView;

	@Mock
	private PlaneController planeController;
	
	@Mock
	private FlightController flightController;
	
	@Mock
	private SearchController searchController;
	
	private static final String ID_FIXTURE_1 = "id1-test";
	private static final String ID_FIXTURE_2 = "id2-test";
	private static final String MODEL_FIXTURE_1 = "model1-test";
	private static final String MODEL_FIXTURE_2 = "model2-test";
	private static final Plane PLANE_FIXTURE_1 = new Plane(ID_FIXTURE_1, MODEL_FIXTURE_1);
	private static final Plane PLANE_FIXTURE_2 = new Plane(ID_FIXTURE_2, MODEL_FIXTURE_2);
	
	
	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this); 
		
		GuiActionRunner.execute(() -> {
			airportSwingView = new AirportSwingView();
			airportSwingView.setAirportController(planeController, flightController, searchController);
			return airportSwingView;
		});
		window = new FrameFixture(robot(), airportSwingView);
		window.show(); 
	}
	
	
	
	@Test @GUITest
	public void testWhenAllIsNotEmptyThenAddButtonShouldBeEnabled() {
		window.panel("panel1").textBox("modelTextBox").enterText("model");		
		window.panel("panel1").button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	
	
	@Test @GUITest
	public void testWhenAttributeIsBlankThenAddButtonShouldBeDisabled() {
		
		JTextComponentFixture modelTextBox = window.textBox("modelTextBox");
		modelTextBox.enterText(" ");
		window.panel("panel1").button(JButtonMatcher.withText("Add")).requireDisabled();
	}
	
	

	@Test @GUITest
	public void testDeleteButtonShouldBeEnabledOnlyWhenAPlaneIsSelected() {
		GuiActionRunner.execute(() -> airportSwingView.getListPlaneModel().addElement(new Plane("model")));
		window.panel("panel1").list("planeList").selectItem(0);
		JButtonFixture deleteButton = window.panel("panel1").button(JButtonMatcher.withText("Delete Selected"));
		deleteButton.requireEnabled();
		window.panel("panel1").list("planeList").clearSelection();
		deleteButton.requireDisabled();
	}
	

	
	@Test @GUITest
	public void testsShowAllPlanesShouldAddPlaneDescriptionsToTheList() {
		GuiActionRunner.execute(
			() -> airportSwingView.showAllPlanes(
					Arrays.asList(PLANE_FIXTURE_1, PLANE_FIXTURE_2))
		);
		String[] listContents = window.panel("panel1").list().contents();
		assertThat(listContents)
			.containsExactly(
					"id=" + ID_FIXTURE_1 + ", " +  "model=" + MODEL_FIXTURE_1,
					"id=" + ID_FIXTURE_2 + ", " +  "model=" + MODEL_FIXTURE_2);
	}
	

	
	@Test @GUITest
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		GuiActionRunner.execute(
			() -> airportSwingView.showPlaneError("error message")
		);
		window.label("errorMessageLabel")
			.requireText("error message");
	}
	
	
	
	@Test @GUITest
	public void testPlaneAddedShouldAddThePlaneToTheListAndResetTheErrorLabel() {
		GuiActionRunner.execute(
				() ->
				airportSwingView.planeAdded(PLANE_FIXTURE_1)
				);
		String[] listContents = window.panel("panel1").list().contents();
		assertThat(listContents).containsExactly("id=" + ID_FIXTURE_1 + ", " +  "model=" + MODEL_FIXTURE_1);
		window.label("errorMessageLabel").requireText(" ");
	}

	
	
	@Test @GUITest
	public void testPlaneRemovedShouldRemoveThePlaneFromTheListAndResetTheErrorLabel() {
		// setup
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<Plane> listPlanesModel = airportSwingView.getListPlaneModel();
				listPlanesModel.addElement(PLANE_FIXTURE_1);
				listPlanesModel.addElement(PLANE_FIXTURE_2);
			}
		);
		
		// execute
		GuiActionRunner.execute(
			() ->
			airportSwingView.planeRemoved(PLANE_FIXTURE_1)
		);
		
		// verify
		String[] listContents = window.panel("panel1").list().contents();
		assertThat(listContents).containsExactly("id=" + ID_FIXTURE_2 + ", " +  "model=" + MODEL_FIXTURE_2);
		window.label("errorMessageLabel").requireText(" ");
	}

	
	
	@Test @GUITest
	public void testAddButtonShouldDelegateToPlaneControllerNewPlane() {

		window.panel("panel1").textBox("modelTextBox").enterText("model");	
		window.panel("panel1").button(JButtonMatcher.withText("Add")).click();
		verify(planeController).newPlane(new Plane("model"));
	}

	
	
	@Test
	public void testDeleteButtonShouldDelegateToPlaneControllerDeletePlane() {
		GuiActionRunner.execute(
			() -> {
				DefaultListModel<Plane> listplanesModel = airportSwingView.getListPlaneModel();
				listplanesModel.addElement(PLANE_FIXTURE_1);
				listplanesModel.addElement(PLANE_FIXTURE_2);
			}
		);
		window.panel("panel1").list("planeList").selectItem(1);
		window.panel("panel1").button(JButtonMatcher.withText("Delete Selected")).click();
		verify(planeController).deletePlane(PLANE_FIXTURE_2);
	}
}


