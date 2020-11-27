package com.airport_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;


@RunWith(GUITestRunner.class)
public class AirportSwingViewFlightPanelE2E extends AssertJSwingJUnitTestCase{

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:4.0.5") 
			.withExposedPorts(27017)
			.withCommand("--replSet rs0");									

	
	private static final String MODEL_FIXTURE = "model-test";
	private static final Plane PLANE_FIXTURE_1 = new Plane(MODEL_FIXTURE);
	private static final Plane PLANE_FIXTURE_2 = new Plane(MODEL_FIXTURE);
	private static final Date DEPARTURE_DATE_FIXTURE = getDate(1);
	private static final Date ARRIVAL_DATE_FIXTURE = getDate(3);
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final Flight FLIGHT_FIXTURE_1 = new Flight(DEPARTURE_DATE_FIXTURE, ARRIVAL_DATE_FIXTURE, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
	private static final Flight FLIGHT_FIXTURE_2 = new Flight(DEPARTURE_DATE_FIXTURE, ARRIVAL_DATE_FIXTURE, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
	
	private static final String DB_NAME = "test-db";
	private static final String PLANE_COLLECTION_NAME = "test-plane-collection";
	private static final String FLIGHT_COLLECTION_NAME = "test-flight-collection";
	
	private MongoClient mongoClient;
	private FrameFixture window;
	
	
	@BeforeClass
	public static void init() throws UnsupportedOperationException, IOException, InterruptedException {
		mongo.start();
		mongo.execInContainer("/bin/bash", "-c", "mongo --eval 'rs.initiate()' --quiet");
		mongo.execInContainer("/bin/bash", "-c",
		            "until mongo --eval 'rs.isMaster()' | grep ismaster | grep true > /dev/null 2>&1;do sleep 1;done");
	}
	
	
	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		mongoClient = new MongoClient(new ServerAddress(containerIpAddress, mappedPort));
		mongoClient.getDatabase(DB_NAME).drop();

		addTestPlaneToDatabase(PLANE_FIXTURE_1);
		addTestPlaneToDatabase(PLANE_FIXTURE_2);
		addTestFlightToDatabase(FLIGHT_FIXTURE_1);
		addTestFlightToDatabase(FLIGHT_FIXTURE_2);
		
		// start the Swing application
		application("com.airport_management.app.App")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-plane-collection=" + PLANE_COLLECTION_NAME,
				"--db-flight-collection=" + FLIGHT_COLLECTION_NAME
			)
			.start();
		
		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Airport Controller".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}
	
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	
	
	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(FLIGHT_FIXTURE_1.getFlightNum(), 
					FLIGHT_FIXTURE_1.getDepartureDate().toString(), 
					FLIGHT_FIXTURE_1.getArrivalDate().toString(),
					FLIGHT_FIXTURE_1.getOrigin(),
					FLIGHT_FIXTURE_1.getDestination(),
					FLIGHT_FIXTURE_1.getPlane().getId(),
					FLIGHT_FIXTURE_1.getPlane().getModel()))
			.anySatisfy(e -> assertThat(e).contains(FLIGHT_FIXTURE_2.getFlightNum(), 
					FLIGHT_FIXTURE_2.getDepartureDate().toString(), 
					FLIGHT_FIXTURE_2.getArrivalDate().toString(),
					FLIGHT_FIXTURE_2.getOrigin(),
					FLIGHT_FIXTURE_2.getDestination(),
					FLIGHT_FIXTURE_2.getPlane().getId(),
					FLIGHT_FIXTURE_2.getPlane().getModel()));
	}
	
	
	@Test @GUITest
	public void testAddFlightButtonSuccess() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(DESTINATION_FIXTURE);
		window.spinner("spinnerDepartureDate").select(getDate(7));
		window.spinner("spinnerArrivalDate").select(getDate(8));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(
					ORIGIN_FIXTURE, 
					DESTINATION_FIXTURE,
					getDate(7).toString(), 
					getDate(8).toString(),
					getAssociatePlane(window.comboBox("planeComboBox").selectedItem()).getId(),
					getAssociatePlane(window.comboBox("planeComboBox").selectedItem()).getModel()));
	}
	
	
	
	@Test @GUITest
	public void testAddFlightButtonErrorOriginAndDestinationAreEquals() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(ORIGIN_FIXTURE);
		window.spinner("spinnerDepartureDate").select(getDate(4));
		window.spinner("spinnerArrivalDate").select(getDate(5));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains("origin or destination is wrong");
	}
	
	
	
	@Test @GUITest
	public void testAddFlightButtonErrorDepartureDateIsAfterArrivalDateOrAreEquals() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(DESTINATION_FIXTURE);
		window.spinner("spinnerDepartureDate").select(getDate(5));
		window.spinner("spinnerArrivalDate").select(getDate(4));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains("departure or arrival date is wrong");
	}
	
	
	
	@Test @GUITest
	public void testAddFlightButtonErrorDepartureOrArrivalDateAreEqualsOfExistingFlight() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(DESTINATION_FIXTURE);
		window.spinner("spinnerDepartureDate").select(DEPARTURE_DATE_FIXTURE);
		window.spinner("spinnerArrivalDate").select(getDate(4));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains("This plane is already in service. Departure or arrival date are equals to exsisting flight.");
	}
	
	
	
	@Test @GUITest
	public void testAddFlightButtonErrorDepartureDateIsBetweenDatesOfExistingFlight() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(DESTINATION_FIXTURE);
		window.spinner("spinnerDepartureDate").select(getDate(2));
		window.spinner("spinnerArrivalDate").select(getDate(4));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains("This plane is already in service. Departure date is between dates of existing flight");
	}
	
	
	
	@Test @GUITest
	public void testAddFlightButtonErrorArrivalDateIsBetweenDatesOfExistingFlight() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(DESTINATION_FIXTURE);
		window.spinner("spinnerDepartureDate").select(getDate(0));
		window.spinner("spinnerArrivalDate").select(getDate(2));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains("This plane is already in service. Arrival date is between dates of existing flight");
	}
	
	
	
	@Test @GUITest
	public void testAddFlightButtonErrorDepartureOrArrivalDateAreEqualsToExistingFlight() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();

		window.comboBox("planeComboBox").selectItem(0);
		window.textBox("originTextBox").enterText(ORIGIN_FIXTURE);
		window.textBox("destinationTextBox").enterText(DESTINATION_FIXTURE);
		window.spinner("spinnerDepartureDate").select(getDate(0));
		window.spinner("spinnerArrivalDate").select(getDate(4));
		
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains("This plane is already in service. Departure date is before and arrival date is after dates of existing flight");
	}
	
	
	
	@Test @GUITest
	public void testDeleteFlightButtonSuccess() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		window.list("flightsList")
			.selectItem(Pattern.compile(".*" + FLIGHT_FIXTURE_1.getFlightNum() + ".*"));
	window.button(JButtonMatcher.withText("Delete Selected")).click();
	assertThat(window.list().contents())
		.noneMatch(e -> e.contains(FLIGHT_FIXTURE_1.getFlightNum()));
	}
	
	
	
	@Test @GUITest
	public void testDeleteFlightButtonError() {
		window.button(JButtonMatcher.withText("Flight Panel")).click();
		//select the flight
		window.list("flightsList")
			.selectItem(Pattern.compile(".*" + FLIGHT_FIXTURE_1.getFlightNum() + ".*"));
		//in the meantime, manually remove the flight from the database
		removeTestFlightFromDatabase(FLIGHT_FIXTURE_1);
		//press the delete button
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		//verify an error is shown
		assertThat(window.label("errorMessageLabel").text())
			.contains(FLIGHT_FIXTURE_1.getFlightNum());
	}
	
	
	
	
	// ############################# private methods ################################
	
	private static final Date getDate(int request) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, request);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	
	
	private Plane getAssociatePlane(String selectedItem) {	
		Document d = mongoClient
				.getDatabase(DB_NAME)
				.getCollection(PLANE_COLLECTION_NAME)
				.find(Filters.eq("_id", new ObjectId(selectedItem))).first();
		return new Plane(""+d.get("_id"),
				""+d.get("model"));
	}
	
	
	
	private void addTestPlaneToDatabase(Plane plane) {
		Document newDocument = new Document();

		mongoClient
		.getDatabase(DB_NAME)
		.getCollection(PLANE_COLLECTION_NAME)
		.insertOne(
				newDocument
				.append("model", plane.getModel()));

		plane.setId(newDocument.get("_id").toString());
	}
	
	
	
	private void addTestFlightToDatabase(Flight flight) {
		Document newDocument = new Document();
		
		mongoClient
		.getDatabase(DB_NAME)
		.getCollection(FLIGHT_COLLECTION_NAME)
		.insertOne(
				newDocument
					.append("departure_date", flight.getDepartureDate())
					.append("arrival_date", flight.getArrivalDate())
					.append("origin", flight.getOrigin())
					.append("destination", flight.getDestination())
					.append("plane_id", flight.getPlane().getId()));
		
		flight.setFlightNum(newDocument.get("_id").toString());
	}
	
	
	
	private void removeTestFlightFromDatabase(Flight flight) {
		String flightNum = flight.getFlightNum();
		mongoClient
		.getDatabase(DB_NAME)
		.getCollection(FLIGHT_COLLECTION_NAME)
		.deleteOne(Filters.eq("_id", new ObjectId(flightNum)));
	}
}


