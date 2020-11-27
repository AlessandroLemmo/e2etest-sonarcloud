package com.airport_management.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.io.IOException;
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

import com.airport_management.model.Plane;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;


@RunWith(GUITestRunner.class)
public class AirportSwingViewPlanePanelE2E extends AssertJSwingJUnitTestCase{

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:4.0.5") 
			.withExposedPorts(27017)
			.withCommand("--replSet rs0");									


	private static final String MODEL_FIXTURE = "model-test";
	private static final Plane PLANE_FIXTURE_1 = new Plane(MODEL_FIXTURE);
	private static final Plane PLANE_FIXTURE_2 = new Plane(MODEL_FIXTURE);
	
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
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(PLANE_FIXTURE_1.getId(), PLANE_FIXTURE_1.getModel()))
			.anySatisfy(e -> assertThat(e).contains(PLANE_FIXTURE_2.getId(), PLANE_FIXTURE_2.getModel()));
	}
	
	
	
	@Test @GUITest
	public void testAddPlaneButtonSuccess() {
		window.button(JButtonMatcher.withText("Plane Panel")).click();
		window.textBox("modelTextBox").enterText(MODEL_FIXTURE);
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(MODEL_FIXTURE));
	}
	
	
	
	@Test @GUITest
	public void testDeletePlaneButtonSuccess() {
		window.button(JButtonMatcher.withText("Plane Panel")).click();
		window.list("planeList")
			.selectItem(Pattern.compile(".*" + PLANE_FIXTURE_1.getId() + ".*"));
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list().contents())
			.noneMatch(e -> e.contains(PLANE_FIXTURE_1.getId()));
	}
	
	
	
	@Test @GUITest
	public void testDeletePlaneButtonError() {
		window.button(JButtonMatcher.withText("Plane Panel")).click();
		// select the student in the list...
		window.list("planeList")
			.selectItem(Pattern.compile(".*" + PLANE_FIXTURE_1.getId() + ".*"));
		// ... in the meantime, manually remove the student from the database
		removeTestPlaneFromDatabase(PLANE_FIXTURE_1);
		// now press the delete button
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		// and verify an error is shown
		assertThat(window.label("errorMessageLabel").text())
			.contains(PLANE_FIXTURE_1.getId());
	}
	

	
	
	// ############################# private methods ################################
	
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
	
	
	
	private void removeTestPlaneFromDatabase(Plane plane) {
		String id = plane.getId();
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(PLANE_COLLECTION_NAME)
			.deleteOne(Filters.eq("_id", new ObjectId(id)));
	}
}


