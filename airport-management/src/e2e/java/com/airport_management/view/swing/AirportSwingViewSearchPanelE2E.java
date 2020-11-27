package com.airport_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;


@RunWith(GUITestRunner.class)
public class AirportSwingViewSearchPanelE2E extends AssertJSwingJUnitTestCase {

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
	private static final Flight FLIGHT_FIXTURE_2 = new Flight(DEPARTURE_DATE_FIXTURE, ARRIVAL_DATE_FIXTURE, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
	
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
	public void testSearchFlightByOriginButtonSuccess() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.textBox("searchOriginTextBox").enterText(ORIGIN_FIXTURE);	
		window.button(JButtonMatcher.withText("Search by origin")).click();
		assertThat(window.list("searchOriginList").contents())
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_1.getFlightNum(),
					FLIGHT_FIXTURE_1.getOrigin(),
					FLIGHT_FIXTURE_1.getDestination(),
					FLIGHT_FIXTURE_1.getDepartureDate().toString(),
					FLIGHT_FIXTURE_1.getArrivalDate().toString(),
					FLIGHT_FIXTURE_1.getPlane().getId(),
					FLIGHT_FIXTURE_1.getPlane().getModel()))
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_2.getFlightNum(),
					FLIGHT_FIXTURE_2.getDepartureDate().toString(), 
					FLIGHT_FIXTURE_2.getArrivalDate().toString(),
					FLIGHT_FIXTURE_2.getOrigin(),
					FLIGHT_FIXTURE_2.getDestination(),
					FLIGHT_FIXTURE_2.getPlane().getId(),
					FLIGHT_FIXTURE_2.getPlane().getModel()));
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByOriginButtonError() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.textBox("searchOriginTextBox").enterText("new-origin");	
		window.button(JButtonMatcher.withText("Search by origin")).click();
		assertThat(window.label("errorSearchFlightLabel").text())
		.contains("There aren't flights with this origin");
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByDestinationButtonSuccess() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.textBox("searchDestinationTextBox").enterText(DESTINATION_FIXTURE);
		window.button(JButtonMatcher.withText("Search by destination")).click();
		assertThat(window.list("searchDestinationList").contents())
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_1.getFlightNum(),
					FLIGHT_FIXTURE_1.getOrigin(),
					FLIGHT_FIXTURE_1.getDestination(),
					FLIGHT_FIXTURE_1.getDepartureDate().toString(),
					FLIGHT_FIXTURE_1.getArrivalDate().toString(),
					FLIGHT_FIXTURE_1.getPlane().getId(),
					FLIGHT_FIXTURE_1.getPlane().getModel()))
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_2.getFlightNum(),
					FLIGHT_FIXTURE_2.getDepartureDate().toString(), 
					FLIGHT_FIXTURE_2.getArrivalDate().toString(),
					FLIGHT_FIXTURE_2.getOrigin(),
					FLIGHT_FIXTURE_2.getDestination(),
					FLIGHT_FIXTURE_2.getPlane().getId(),
					FLIGHT_FIXTURE_2.getPlane().getModel()));
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByDestinationButtonError() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.textBox("searchOriginTextBox").enterText("new-destination");	
		window.button(JButtonMatcher.withText("Search by destination")).click();
		assertThat(window.label("errorSearchFlightLabel").text())
		.contains("There aren't flights with this destination");
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByDepartureDateInRangeButtonSuccess() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.spinner("spinnerSearchByDepartureDateStart").select(getDate(0));
		window.spinner("spinnerSearchByDepartureDateEnd").select(getDate(2));
		window.button(JButtonMatcher.withText("Search by departure date")).click();
		assertThat(window.list("searchDepartureDateList").contents())
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_1.getFlightNum(),
					FLIGHT_FIXTURE_1.getOrigin(),
					FLIGHT_FIXTURE_1.getDestination(),
					FLIGHT_FIXTURE_1.getDepartureDate().toString(),
					FLIGHT_FIXTURE_1.getArrivalDate().toString(),
					FLIGHT_FIXTURE_1.getPlane().getId(),
					FLIGHT_FIXTURE_1.getPlane().getModel()))
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_2.getFlightNum(),
					FLIGHT_FIXTURE_2.getDepartureDate().toString(), 
					FLIGHT_FIXTURE_2.getArrivalDate().toString(),
					FLIGHT_FIXTURE_2.getOrigin(),
					FLIGHT_FIXTURE_2.getDestination(),
					FLIGHT_FIXTURE_2.getPlane().getId(),
					FLIGHT_FIXTURE_2.getPlane().getModel()));
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByDepartureDateInRangeButtonError() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.spinner("spinnerSearchByDepartureDateStart").select(getDate(4));
		window.spinner("spinnerSearchByDepartureDateEnd").select(getDate(5));
		window.button(JButtonMatcher.withText("Search by departure date")).click();
		assertThat(window.label("errorSearchFlightLabel").text())
			.contains("There aren't flights with departure date in the selected range");
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByArrivalDateInRangeButtonSuccess() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.spinner("spinnerSearchByArrivalDateStart").select(getDate(2));
		window.spinner("spinnerSearchByArrivalDateEnd").select(getDate(4));
		window.button(JButtonMatcher.withText("Search by arrival date")).click();
		assertThat(window.list("searchArrivalDateList").contents())
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_1.getFlightNum(),
					FLIGHT_FIXTURE_1.getOrigin(),
					FLIGHT_FIXTURE_1.getDestination(),
					FLIGHT_FIXTURE_1.getDepartureDate().toString(),
					FLIGHT_FIXTURE_1.getArrivalDate().toString(),
					FLIGHT_FIXTURE_1.getPlane().getId(),
					FLIGHT_FIXTURE_1.getPlane().getModel()))
			.anySatisfy(e -> assertThat(e).contains(
					FLIGHT_FIXTURE_2.getFlightNum(),
					FLIGHT_FIXTURE_2.getDepartureDate().toString(), 
					FLIGHT_FIXTURE_2.getArrivalDate().toString(),
					FLIGHT_FIXTURE_2.getOrigin(),
					FLIGHT_FIXTURE_2.getDestination(),
					FLIGHT_FIXTURE_2.getPlane().getId(),
					FLIGHT_FIXTURE_2.getPlane().getModel()));
	}
	
	
	
	@Test @GUITest
	public void testSearchFlightByArrivalDateInRangeButtonError() {
		window.button(JButtonMatcher.withText("Flight Search")).click();
		window.spinner("spinnerSearchByArrivalDateStart").select(getDate(4));
		window.spinner("spinnerSearchByArrivalDateEnd").select(getDate(5));
		window.button(JButtonMatcher.withText("Search by arrival date")).click();
		assertThat(window.label("errorSearchFlightLabel").text())
			.contains("There aren't flights with arrival date in the selected range");
	}
	
	
	
	@Test @GUITest
	public void testSearchPlaneAssociatedFlightsButtonSuccess() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		window.comboBox("planeComboBoxSearch").selectItem(0);
		window.button(JButtonMatcher.withText("Search associates flights")).click();
		assertThat(window.list("searchFlightsAssociatesList").contents())
		.anySatisfy(e -> assertThat(e).contains(
				FLIGHT_FIXTURE_1.getFlightNum(),
				FLIGHT_FIXTURE_1.getOrigin(),
				FLIGHT_FIXTURE_1.getDestination(),
				FLIGHT_FIXTURE_1.getDepartureDate().toString(),
				FLIGHT_FIXTURE_1.getArrivalDate().toString(),
				FLIGHT_FIXTURE_1.getPlane().getId(),
				FLIGHT_FIXTURE_1.getPlane().getModel()));
	}
	
	
	
	@Test @GUITest
	public void testSearchPlaneAssociatedFlightsButtonError() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		window.comboBox("planeComboBoxSearch").selectItem(1);
		window.button(JButtonMatcher.withText("Search associates flights")).click();
		assertThat(window.label("errorSearchPlaneLabel").text())
			.contains("There aren't flights associates with selected plane");
	}
	
	
	
	@Test @GUITest
	public void testSearchPlaneByModelButtonSuccess() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		window.textBox("searchModelTextBox").enterText(MODEL_FIXTURE);	
		window.button(JButtonMatcher.withText("Search by model")).click();
		assertThat(window.list("searchPlaneByModelList").contents())
			.anySatisfy(e -> assertThat(e).contains(
					PLANE_FIXTURE_1.getId(),
					PLANE_FIXTURE_1.getModel()))
			.anySatisfy(e -> assertThat(e).contains(
					PLANE_FIXTURE_2.getId(),
					PLANE_FIXTURE_2.getModel()));
	}
	
	
	
	@Test @GUITest
	public void testSearchPlaneByModelButtonError() {
		window.button(JButtonMatcher.withText("Plane Search")).click();
		window.textBox("searchModelTextBox").enterText("new-model");	
		window.button(JButtonMatcher.withText("Search by model")).click();
		assertThat(window.label("errorSearchPlaneLabel").text())
			.contains("There aren't planes with insert model");
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
}


