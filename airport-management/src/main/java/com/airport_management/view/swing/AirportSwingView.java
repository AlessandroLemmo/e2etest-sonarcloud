package com.airport_management.view.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import com.airport_management.view.FlightView;
import com.airport_management.view.PlaneView;
import com.airport_management.view.SearchView;
import com.airport_management.controller.FlightController;
import com.airport_management.controller.PlaneController;
import com.airport_management.controller.SearchController;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class AirportSwingView extends JFrame implements PlaneView, FlightView, SearchView{

	private static final long serialVersionUID = 1L;

	private transient PlaneController planeController;
	private transient FlightController flightController;
	private transient SearchController searchController;
	
	private JLayeredPane layeredPane;
	private JPanel contentPane;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;

	private DefaultListModel<Plane> listPlanesModel;
	private DefaultListModel<Flight> listFlightsModel;
	private DefaultListModel<Flight> listFoundedFlightsByOriginModel;
	private DefaultListModel<Flight> listFoundedFlightsByDestinationModel;
	private DefaultListModel<Flight> listFoundedFlightsByDepartureDateModel;
	private DefaultListModel<Flight> listFoundedFlightsByArrivalDateModel;
	private DefaultListModel<Flight> listFoundedFlightsAssociatesWithPlaneModel;
	private DefaultListModel<Plane> listFoundedPlanesByModel;
	
	private JList<Plane> listPlanes;
	private JList<Flight> listFlights;
	private JList<Flight> listSearchOrigin;
	private JList<Flight> listSearchDestination;
	private JList<Flight> listSearchDepartureDate;
	private JList<Flight> listSearchArrivalDate;
	private JList<Flight> listSearchFlightsAssociates;
	private JList<Plane> listSearchPlaneByModel;

	private JScrollPane scrollPane;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;
	private JScrollPane scrollPane3;
	private JScrollPane scrollPane4;
	private JScrollPane scrollPane5;
	private JScrollPane scrollPane6;
	private JScrollPane scrollPane7;

	private JTextField txtModel;
	private JTextField txtOrigin;
	private JTextField txtDestination;
	private JTextField txtSearchOrigin;
	private JTextField txtSearchDestination;
	private JTextField txtSearchModel;
	
	private JLabel lblErrorMessage;
	private JLabel lblModel;
	private JLabel lblOrigin;
	private JLabel lblErrorMessageFlight;
	private JLabel lblSearchFlightOrigin;
	private JLabel lblErrorMessageSearch;
	private JLabel lblRangeDepartureDate;
	private JLabel lblSearchFlightDestination;
	private JLabel lblSearchFlightDepartureDateInRange;
	private JLabel lblSearchFlightArrivalDateInRange;
	private JLabel lblRangeArrivalDate;
	private JLabel lblSearchFlightsAssociates;
	private JLabel lblErrorMessageSearchPlane;
	private JLabel lblPlaneModel;
	
	private JButton btnAdd;
	private JButton btnPlanePanel;
	private JButton btnFlightPanel;
	private JButton btnAddFlight;
	private JButton btnDeleteSelected2;
	private JButton btnSearchByDestination;
	private JButton btnSearchPanel;
	private JButton btnSearchByOrigin;
	private JButton btnSearchByDepartureDate;
	private JButton btnSearchByArrivalDate;
	private JButton btnPlaneSearch;
	private JButton btnSearchAssociatesFlights;
	private JButton btnSearchByModel;
	
	private JSpinner spinnerDepartureDate;
	private JSpinner spinnerArrivalDate;
	private JSpinner spinnerSearchByDepartureDateStart;
	private JSpinner spinnerSearchByDepartureDateEnd;
	private JSpinner spinnerSearchByArrivalDateStart;
	private JSpinner spinnerSearchByArrivalDateEnd;
	
	private JComboBox<String> comboBox;
	private JComboBox<String> comboBoxSearch;
	
	private transient List<Plane> planes = new ArrayList<>();
	private JLabel lblPlane;
	private JLabel lblDestination;
	private JLabel lblDepartureDate;
	private JButton btnDeleteSelected;
	


	public JComboBox<String> getComboBox() {
		return comboBox;
	}
	
	public JComboBox<String> getComboBoxSearch() {
		return comboBoxSearch;
	}
	
	public JSpinner getSpinnerDepartureDate() {
		return spinnerDepartureDate;
	}
	
	public JSpinner getSpinnerArrivalDate() {
		return spinnerArrivalDate;
	}
	
	public JSpinner getSpinnerSearchByDepartureDateStart() {
		return spinnerSearchByDepartureDateStart;
	}
	
	public JSpinner getSpinnerSearchByDepartureDateEnd() {
		return spinnerSearchByDepartureDateEnd;
	}
	
	public JSpinner getSpinnerSearchByArrivalDateStart() {
		return spinnerSearchByArrivalDateStart;
	}
	
	public JSpinner getSpinnerSearchByArrivalDateEnd() {
		return spinnerSearchByArrivalDateEnd;
	}
	
	
	DefaultListModel<Plane> getListPlaneModel() {
		return listPlanesModel;
	}
	
	DefaultListModel<Flight> getListFlightsModel() {
		return listFlightsModel;
	}

	
	public void setAirportController(PlaneController planeController, FlightController flightController, SearchController searchController) {
		this.planeController = planeController;
		this.flightController = flightController;
		this.searchController = searchController;
	}

	
	public void switchPanels(JPanel panel) {
		layeredPane.removeAll();
		layeredPane.add(panel);
		layeredPane.repaint();
		layeredPane.revalidate();
	}
	
	
	
	/**
	 * Create the frame.
	 * @throws ParseException 
	 */

	public AirportSwingView() throws ParseException {

		setTitle("Airport Controller");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 982, 817);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{54, 124, 124, 127, 122, 404, 49, 0};
		gbl_contentPane.rowHeights = new int[]{25, 35, 708, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		
		//setup of spinner date
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		
		cal.add(Calendar.YEAR, -1); 
		Date previousYearFromNow = cal.getTime();
		
		cal.add(Calendar.YEAR, 2); 
		Date nextYearFromNow = cal.getTime();
		
		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date oneSecondBeforeMidnight = cal.getTime();
		
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date tomorrow = cal.getTime();

		cal.add(Calendar.YEAR, 1); 
		Date nextYear = cal.getTime();
		
		
		
		
		
		/*
		 * ########### switch panel #############
		 * 
		 */
		
		//button plane panel
		btnPlanePanel = new JButton("Plane Panel");
		btnPlanePanel.addActionListener(arg0 -> switchPanels(panel1));
		GridBagConstraints gbc_btnPlanePanel = new GridBagConstraints();
		gbc_btnPlanePanel.anchor = GridBagConstraints.NORTH;
		gbc_btnPlanePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPlanePanel.insets = new Insets(0, 0, 5, 5);
		gbc_btnPlanePanel.gridx = 1;
		gbc_btnPlanePanel.gridy = 0;
		contentPane.add(btnPlanePanel, gbc_btnPlanePanel);
		
		
		//button flight panel
		btnFlightPanel = new JButton("Flight Panel");
		btnFlightPanel.addActionListener(arg0 -> switchPanels(panel2));
		GridBagConstraints gbc_btnFlightPanel = new GridBagConstraints();
		gbc_btnFlightPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnFlightPanel.insets = new Insets(0, 0, 5, 5);
		gbc_btnFlightPanel.gridx = 2;
		gbc_btnFlightPanel.gridy = 0;
		contentPane.add(btnFlightPanel, gbc_btnFlightPanel);
		
		
		//button flight panel add action combo box options
		btnFlightPanel.addActionListener(arg0 -> {	
			planes = planeController.returnAllPlanes();	
			comboBox.removeAllItems();
			for(int i = 0; i < planes.size(); i++)
				comboBox.addItem(planes.get(i).getId());
			comboBox.setSelectedIndex(-1);

		});		
		
		
		//button search flight panel
		btnSearchPanel = new JButton("Flight Search");
		btnSearchPanel.addActionListener(arg0 -> switchPanels(panel3));
		GridBagConstraints gbc_btnSearchPanel = new GridBagConstraints();
		gbc_btnSearchPanel.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearchPanel.gridx = 3;
		gbc_btnSearchPanel.gridy = 0;
		contentPane.add(btnSearchPanel, gbc_btnSearchPanel);
		
		
		//button search plane panel
		btnPlaneSearch = new JButton("Plane Search");
		btnPlaneSearch.addActionListener(arg0 -> switchPanels(panel4));
		GridBagConstraints gbc_btnPlaneSearch = new GridBagConstraints();
		gbc_btnPlaneSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnPlaneSearch.gridx = 4;
		gbc_btnPlaneSearch.gridy = 0;
		contentPane.add(btnPlaneSearch, gbc_btnPlaneSearch);
		
		
		//button search plane panel add action listener
		btnPlaneSearch.addActionListener(arg0 -> {
			planes = planeController.returnAllPlanes();
			comboBoxSearch.removeAllItems();
			for(int i = 0; i < planes.size(); i++)
				comboBoxSearch.addItem(planes.get(i).getId());
			comboBoxSearch.setSelectedIndex(-1);
			
		});
		
		
		//layered pane
		layeredPane = new JLayeredPane();
		GridBagConstraints gbc_layeredPane = new GridBagConstraints();
		gbc_layeredPane.insets = new Insets(0, 0, 0, 5);
		gbc_layeredPane.fill = GridBagConstraints.BOTH;
		gbc_layeredPane.gridwidth = 5;
		gbc_layeredPane.gridx = 1;
		gbc_layeredPane.gridy = 2;
		contentPane.add(layeredPane, gbc_layeredPane);
		layeredPane.setLayout(new CardLayout(0, 0));
		

		
		
		
		/*
		 * ########### plane panel ############ 
		 * 
		 */
				
		//panel 1
		panel1 = new JPanel();
		panel1.setName("panel1");
		layeredPane.add(panel1, "name_518306635232");


		//for activate Add plane button
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAdd.setEnabled(
					!txtModel.getText().trim().isEmpty() 
				);
			}
		};
		GridBagLayout gbl_panel1 = new GridBagLayout();
		gbl_panel1.columnWidths = new int[]{101, 78, 34, 104, 56, 555, 0};
		gbl_panel1.rowHeights = new int[]{19, 19, 19, 15, 25, 25, 433, 25, 15, 0};
		gbl_panel1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel1.setLayout(gbl_panel1);
		
		
		//model label
		lblModel = new JLabel("model");
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.anchor = GridBagConstraints.EAST;
		gbc_lblModel.insets = new Insets(0, 0, 5, 5);
		gbc_lblModel.gridx = 0;
		gbc_lblModel.gridy = 0;
		panel1.add(lblModel, gbc_lblModel);
		
		
		//model text
		txtModel = new JTextField();
		txtModel.setName("modelTextBox");
		txtModel.addKeyListener(btnAddEnabler);
		GridBagConstraints gbc_txtModel = new GridBagConstraints();
		gbc_txtModel.anchor = GridBagConstraints.NORTH;
		gbc_txtModel.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtModel.insets = new Insets(0, 0, 5, 0);
		gbc_txtModel.gridwidth = 5;
		gbc_txtModel.gridx = 1;
		gbc_txtModel.gridy = 0;
		panel1.add(txtModel, gbc_txtModel);
		txtModel.setColumns(10);
				
				
		//button delete plane
		btnDeleteSelected = new JButton("Delete Selected");
		btnDeleteSelected.setEnabled(false);
		btnDeleteSelected.addActionListener(
				e -> planeController.deletePlane(listPlanes.getSelectedValue()));
		GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
		gbc_btnDeleteSelected.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteSelected.gridx = 5;
		gbc_btnDeleteSelected.gridy = 7;
		panel1.add(btnDeleteSelected, gbc_btnDeleteSelected);	
		
		
		//button add plane
		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 5;
		gbc_btnAdd.gridy = 1;
		panel1.add(btnAdd, gbc_btnAdd);
		btnAdd.addActionListener(
				e -> planeController.newPlane(
						new Plane(txtModel.getText()))); 
				
		
		//list flights
		listPlanesModel = new DefaultListModel<>();
		//scroll list flights
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 5;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridwidth = 6;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		panel1.add(scrollPane, gbc_scrollPane);
		listPlanes = new JList<>(listPlanesModel);
		listPlanes.addListSelectionListener(
				e -> btnDeleteSelected.setEnabled(listPlanes.getSelectedIndex() != -1));
		listPlanes.setName("planeList");
		scrollPane.setViewportView(listPlanes);
		listPlanes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		//error message
		lblErrorMessage = new JLabel(" ");
		lblErrorMessage.setForeground(Color.RED);
		lblErrorMessage.setName("errorMessageLabel");
		GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
		gbc_lblErrorMessage.anchor = GridBagConstraints.NORTH;
		gbc_lblErrorMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblErrorMessage.gridwidth = 6;
		gbc_lblErrorMessage.gridx = 0;
		gbc_lblErrorMessage.gridy = 8;
		panel1.add(lblErrorMessage, gbc_lblErrorMessage);
				
				
		
		
		
		
		/*
		 * ########### flight panel ###########
		 * 
		 */

		//panel 2
		panel2 = new JPanel();
		panel2.setName("panel2");
		layeredPane.add(panel2, "name_520876488608");


		//for activate Add flight button
		KeyAdapter btnAddFlightEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnAddFlight.setEnabled(
					!txtOrigin.getText().trim().isEmpty() &&
					!txtDestination.getText().trim().isEmpty() &&
					comboBox.getSelectedItem() != null 	
				);
			}
		};
		GridBagLayout gbl_panel2 = new GridBagLayout();
		gbl_panel2.columnWidths = new int[]{101, 78, 34, 104, 56, 526, 0};
		gbl_panel2.rowHeights = new int[]{19, 19, 19, 15, 25, 25, 433, 25, 15, 0};
		gbl_panel2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel2.setLayout(gbl_panel2);
		
		
		//plane label
		lblPlane = new JLabel("plane");
		GridBagConstraints gbc_lblPlane = new GridBagConstraints();
		gbc_lblPlane.insets = new Insets(0, 0, 5, 5);
		gbc_lblPlane.anchor = GridBagConstraints.EAST;
		gbc_lblPlane.gridx = 0;
		gbc_lblPlane.gridy = 0;
		panel2.add(lblPlane, gbc_lblPlane);


		//plane id combo box
		comboBox = new JComboBox<>();
		comboBox.setName("planeComboBox");
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.BOTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.gridwidth = 3;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		panel2.add(comboBox, gbc_comboBox);
		comboBox.addKeyListener(btnAddFlightEnabler);
		comboBox.setBackground(Color.WHITE);

		
		//origin label
		lblOrigin = new JLabel("origin");
		GridBagConstraints gbc_lblOrigin = new GridBagConstraints();
		gbc_lblOrigin.anchor = GridBagConstraints.EAST;
		gbc_lblOrigin.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrigin.gridx = 0;
		gbc_lblOrigin.gridy = 1;
		panel2.add(lblOrigin, gbc_lblOrigin);


		//origin text
		txtOrigin = new JTextField();
		txtOrigin.setName("originTextBox");
		txtOrigin.addKeyListener(btnAddFlightEnabler);
		GridBagConstraints gbc_txtOrigin = new GridBagConstraints();
		gbc_txtOrigin.anchor = GridBagConstraints.NORTH;
		gbc_txtOrigin.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOrigin.insets = new Insets(0, 0, 5, 0);
		gbc_txtOrigin.gridwidth = 5;
		gbc_txtOrigin.gridx = 1;
		gbc_txtOrigin.gridy = 1;
		panel2.add(txtOrigin, gbc_txtOrigin);
		txtOrigin.setColumns(10);


		//destination label		
		lblDestination = new JLabel("destination");
		GridBagConstraints gbc_lblDestination = new GridBagConstraints();
		gbc_lblDestination.insets = new Insets(0, 0, 5, 5);
		gbc_lblDestination.anchor = GridBagConstraints.EAST;
		gbc_lblDestination.gridx = 0;
		gbc_lblDestination.gridy = 2;
		panel2.add(lblDestination, gbc_lblDestination);
		
		
		//destination text
		txtDestination = new JTextField();
		txtDestination.setName("destinationTextBox");
		txtDestination.addKeyListener(btnAddFlightEnabler);
		GridBagConstraints gbc_txtDestination = new GridBagConstraints();
		gbc_txtDestination.anchor = GridBagConstraints.NORTH;
		gbc_txtDestination.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDestination.insets = new Insets(0, 0, 5, 0);
		gbc_txtDestination.gridwidth = 5;
		gbc_txtDestination.gridx = 1;
		gbc_txtDestination.gridy = 2;
		panel2.add(txtDestination, gbc_txtDestination);
		txtDestination.setColumns(10);
		
		
		//departure date label
		lblDepartureDate = new JLabel("departure date");
		GridBagConstraints gbc_lblDepartureDate = new GridBagConstraints();
		gbc_lblDepartureDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblDepartureDate.gridx = 0;
		gbc_lblDepartureDate.gridy = 3;
		panel2.add(lblDepartureDate, gbc_lblDepartureDate);
		
		
		//arrival date label
		JLabel lblArrivalDate = new JLabel("arrival date");
		GridBagConstraints gbc_lblArrivalDate = new GridBagConstraints();
		gbc_lblArrivalDate.anchor = GridBagConstraints.NORTH;
		gbc_lblArrivalDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblArrivalDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblArrivalDate.gridx = 3;
		gbc_lblArrivalDate.gridy = 3;
		panel2.add(lblArrivalDate, gbc_lblArrivalDate);
		
		
		//departure date spinner
		spinnerDepartureDate = new JSpinner();
		spinnerDepartureDate.setName("spinnerDepartureDate");
		spinnerDepartureDate.setModel(new SpinnerDateModel(tomorrow, oneSecondBeforeMidnight, nextYear, Calendar.DAY_OF_MONTH));
		GridBagConstraints gbc_spinnerDepartureDate = new GridBagConstraints();
		gbc_spinnerDepartureDate.fill = GridBagConstraints.BOTH;
		gbc_spinnerDepartureDate.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDepartureDate.gridwidth = 2;
		gbc_spinnerDepartureDate.gridx = 0;
		gbc_spinnerDepartureDate.gridy = 4;
		panel2.add(spinnerDepartureDate, gbc_spinnerDepartureDate);
		
		
		//arrival date spinner
		spinnerArrivalDate = new JSpinner();
		spinnerArrivalDate.setName("spinnerArrivalDate");
		spinnerArrivalDate.setModel( new SpinnerDateModel(tomorrow, oneSecondBeforeMidnight, nextYear, Calendar.DAY_OF_MONTH));
		GridBagConstraints gbc_spinnerArrivalDate = new GridBagConstraints();
		gbc_spinnerArrivalDate.anchor = GridBagConstraints.WEST;
		gbc_spinnerArrivalDate.fill = GridBagConstraints.VERTICAL;
		gbc_spinnerArrivalDate.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerArrivalDate.gridwidth = 3;
		gbc_spinnerArrivalDate.gridx = 3;
		gbc_spinnerArrivalDate.gridy = 4;
		panel2.add(spinnerArrivalDate, gbc_spinnerArrivalDate);
		
				
		//button delete flight
		btnDeleteSelected2 = new JButton("Delete Selected");
		btnDeleteSelected2.setEnabled(false);
		btnDeleteSelected2.addActionListener(
				e -> flightController.deleteFlight(listFlights.getSelectedValue()));
				
						
		//button add flight
		btnAddFlight = new JButton("Add");
		btnAddFlight.setEnabled(false);
		GridBagConstraints gbc_btnAddFlight = new GridBagConstraints();
		gbc_btnAddFlight.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAddFlight.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddFlight.gridx = 5;
		gbc_btnAddFlight.gridy = 5;
		panel2.add(btnAddFlight, gbc_btnAddFlight);
		btnAddFlight.addActionListener(
				e -> flightController.newFlight(
						new Flight(
								(Date)spinnerDepartureDate.getValue(),
								(Date)spinnerArrivalDate.getValue(),
								txtOrigin.getText(), 
								txtDestination.getText(), 
								planeController.idPlane((String)comboBox.getSelectedItem())))); 
				
		
		//list flights
		listFlightsModel = new DefaultListModel<>();
		//scroll list flights
		scrollPane1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.gridwidth = 6;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 6;
		panel2.add(scrollPane1, gbc_scrollPane_1);
		listFlights = new JList<>(listFlightsModel);
		listFlights.addListSelectionListener(
				e -> btnDeleteSelected2.setEnabled(listFlights.getSelectedIndex() != -1));
		listFlights.setName("flightsList");
		scrollPane1.setViewportView(listFlights);
		listFlights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_btnDeleteSelected2 = new GridBagConstraints();
		gbc_btnDeleteSelected2.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnDeleteSelected2.insets = new Insets(0, 0, 5, 0);
		gbc_btnDeleteSelected2.gridx = 5;
		gbc_btnDeleteSelected2.gridy = 7;
		panel2.add(btnDeleteSelected2, gbc_btnDeleteSelected2);		


		//error message
		lblErrorMessageFlight = new JLabel(" ");
		lblErrorMessageFlight.setForeground(Color.RED);
		lblErrorMessageFlight.setName("errorMessageLabel");
		GridBagConstraints gbc_lblErrorMessageFlight = new GridBagConstraints();
		gbc_lblErrorMessageFlight.anchor = GridBagConstraints.NORTH;
		gbc_lblErrorMessageFlight.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblErrorMessageFlight.gridwidth = 6;
		gbc_lblErrorMessageFlight.gridx = 0;
		gbc_lblErrorMessageFlight.gridy = 8;
		panel2.add(lblErrorMessageFlight, gbc_lblErrorMessageFlight);

		
			

		
		/*
		 * ########### Search Flight panel #############
		 * 
		 */
		
		//panel 3
		panel3 = new JPanel();
		panel3.setName("panel3");
		layeredPane.add(panel3, "name_5447417071062");
		panel3.setLayout(null);
		
		
		//search by origin label
		lblSearchFlightOrigin = new JLabel("Flight origin");
		lblSearchFlightOrigin.setBounds(1, -1, 170, 15);
		panel3.add(lblSearchFlightOrigin);
		
		
		//search by origin text
		txtSearchOrigin = new JTextField();
		txtSearchOrigin.setName("searchOriginTextBox");
		txtSearchOrigin.setBounds(1, 23, 686, 19);
		panel3.add(txtSearchOrigin);
		txtSearchOrigin.setColumns(10);
		
		
		//scroll list search by origin
		scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(1, 50, 909, 94);
		panel3.add(scrollPane2);
		
		
		//list search by origin
		listFoundedFlightsByOriginModel = new DefaultListModel<>();
		listSearchOrigin = new JList<>(listFoundedFlightsByOriginModel);
		listSearchOrigin.setName("searchOriginList");
		scrollPane2.setViewportView(listSearchOrigin);

		
		//button search by origin
		btnSearchByOrigin = new JButton("Search by origin");
		btnSearchByOrigin.setBounds(692, 20, 217, 25);
		panel3.add(btnSearchByOrigin);
		btnSearchByOrigin.addActionListener(
				e -> searchController.findAllFlightsByOrigin(txtSearchOrigin.getText()));
		
		
		//########################################################################
		
		
		//search by destination label
		lblSearchFlightDestination = new JLabel("Flight destination");
		lblSearchFlightDestination.setBounds(1, 171, 157, 15);
		panel3.add(lblSearchFlightDestination);
		
		
		//search by destination text
		txtSearchDestination = new JTextField();
		txtSearchDestination.setName("searchDestinationTextBox");
		txtSearchDestination.setBounds(1, 193, 682, 19);
		panel3.add(txtSearchDestination);
		txtSearchDestination.setColumns(10);
		
		
		//scroll list search by destination
		scrollPane3 = new JScrollPane();
		scrollPane3.setBounds(1, 221, 908, 94);
		panel3.add(scrollPane3);
		
		
		//list search by destination
		listFoundedFlightsByDestinationModel = new DefaultListModel<>();
		listSearchDestination = new JList<>(listFoundedFlightsByDestinationModel);
		listSearchDestination.setName("searchDestinationList");
		scrollPane3.setViewportView(listSearchDestination);
		
		
		//button search by destination
		btnSearchByDestination = new JButton("Search by destination");
		btnSearchByDestination.setBounds(690, 190, 217, 25);
		panel3.add(btnSearchByDestination);
		btnSearchByDestination.addActionListener(
				e -> searchController.findAllFlightsByDestination(txtSearchDestination.getText()));
		
		
		//#####################################################################
		
		
		//search by departure date in range label
		lblSearchFlightDepartureDateInRange = new JLabel("Flight departure date in range");
		lblSearchFlightDepartureDateInRange.setBounds(1, 340, 258, 15);
		panel3.add(lblSearchFlightDepartureDateInRange);
		
		
		//spinner search by departure date start
		spinnerSearchByDepartureDateStart = new JSpinner();
		spinnerSearchByDepartureDateStart.setName("spinnerSearchByDepartureDateStart");
		spinnerSearchByDepartureDateStart.setModel(new SpinnerDateModel(now, previousYearFromNow, nextYearFromNow, Calendar.DAY_OF_YEAR));
		spinnerSearchByDepartureDateStart.setBounds(1, 361, 147, 20);
		panel3.add(spinnerSearchByDepartureDateStart);


		//range label
		lblRangeDepartureDate = new JLabel("÷");
		lblRangeDepartureDate.setName("lblRangeDepartureDate");
		lblRangeDepartureDate.setBounds(162, 363, 20, 15);
		panel3.add(lblRangeDepartureDate);


		//spinner search by departure date end
		spinnerSearchByDepartureDateEnd = new JSpinner();
		spinnerSearchByDepartureDateEnd.setName("spinnerSearchByDepartureDateEnd");
		spinnerSearchByDepartureDateEnd.setModel(new SpinnerDateModel(now, previousYearFromNow, nextYearFromNow, Calendar.DAY_OF_YEAR));
		spinnerSearchByDepartureDateEnd.setBounds(186, 361, 147, 20);
		panel3.add(spinnerSearchByDepartureDateEnd);


		//scroll list search by departure date in range
		scrollPane4 = new JScrollPane();
		scrollPane4.setBounds(1, 390, 907, 94);
		panel3.add(scrollPane4);

		
		//list search by departure date in range
		listFoundedFlightsByDepartureDateModel = new DefaultListModel<>();
		listSearchDepartureDate = new JList<>(listFoundedFlightsByDepartureDateModel);
		listSearchDepartureDate.setName("searchDepartureDateList");
		scrollPane4.setViewportView(listSearchDepartureDate);


		//button search by departure date in range
		btnSearchByDepartureDate = new JButton("Search by departure date");
		btnSearchByDepartureDate.setBounds(690, 358, 217, 25);
		panel3.add(btnSearchByDepartureDate);
		btnSearchByDepartureDate.addActionListener(
				e -> searchController.findAllFlightsWithDepartureDateInRange((Date)spinnerSearchByDepartureDateStart.getValue(), (Date)spinnerSearchByDepartureDateEnd.getValue()));


		//#######################################################################
		
		
		//search by arrival date in range label
		lblSearchFlightArrivalDateInRange = new JLabel("Flight arrival date in range");
		lblSearchFlightArrivalDateInRange.setBounds(2, 518, 211, 15);
		panel3.add(lblSearchFlightArrivalDateInRange);
		
		
		//spinner search by arrival date start
		spinnerSearchByArrivalDateStart = new JSpinner();
		spinnerSearchByArrivalDateStart.setModel(new SpinnerDateModel(now, previousYearFromNow, nextYearFromNow, Calendar.DAY_OF_YEAR));
		spinnerSearchByArrivalDateStart.setName("spinnerSearchByArrivalDateStart");
		spinnerSearchByArrivalDateStart.setBounds(1, 539, 147, 20);
		panel3.add(spinnerSearchByArrivalDateStart);
		
		
		//range label
		lblRangeArrivalDate = new JLabel("÷");
		lblRangeArrivalDate.setName("lblRangeArrivalDate");
		lblRangeArrivalDate.setBounds(162, 541, 20, 15);
		panel3.add(lblRangeArrivalDate);
		
		
		//spinner search by arrival date end
		spinnerSearchByArrivalDateEnd = new JSpinner();
		spinnerSearchByArrivalDateEnd.setName("spinnerSearchByArrivalDateEnd");
		spinnerSearchByArrivalDateEnd.setModel(new SpinnerDateModel(now, previousYearFromNow, nextYearFromNow, Calendar.DAY_OF_YEAR));
		spinnerSearchByArrivalDateEnd.setBounds(186, 539, 147, 20);
		panel3.add(spinnerSearchByArrivalDateEnd);
		
		
		//scroll list search by arrival date
		scrollPane5 = new JScrollPane();
		scrollPane5.setBounds(1, 568, 907, 94);
		panel3.add(scrollPane5);
		
		
		//list search by arrival date
		listFoundedFlightsByArrivalDateModel = new DefaultListModel<>();
		listSearchArrivalDate = new JList<>(listFoundedFlightsByArrivalDateModel);
		listSearchArrivalDate.setName("searchArrivalDateList");
		scrollPane5.setViewportView(listSearchArrivalDate);
		
		
		//button search by arrival date
		btnSearchByArrivalDate = new JButton("Search by arrival date");
		btnSearchByArrivalDate.setBounds(690, 536, 217, 25);
		panel3.add(btnSearchByArrivalDate);
		btnSearchByArrivalDate.addActionListener(
				e -> searchController.findAllFlightsWithArrivalDateInRange((Date)spinnerSearchByArrivalDateStart.getValue(), (Date)spinnerSearchByArrivalDateEnd.getValue()));
		
		
		//error search label
		lblErrorMessageSearch = new JLabel(" ");
		lblErrorMessageSearch.setName("errorSearchFlightLabel");
		lblErrorMessageSearch.setForeground(Color.RED);
		lblErrorMessageSearch.setBounds(12, 681, 883, 15);
		panel3.add(lblErrorMessageSearch);
		

		
		
		
		/*
		 * ########### Search Plane panel #############
		 * 
		 */
		
		//panel 4
		panel4 = new JPanel();
		panel4.setName("panel4");
		layeredPane.add(panel4, "name_1231722096124");
		panel4.setLayout(null);
		
		
		//search flights associates label
		lblSearchFlightsAssociates = new JLabel("Search flights associates with the plane");
		lblSearchFlightsAssociates.setBounds(2, 12, 366, 15);
		panel4.add(lblSearchFlightsAssociates);
		
		
		//planes id combo box
		comboBoxSearch = new JComboBox<>();
		comboBoxSearch.setName("planeComboBoxSearch");
		comboBoxSearch.setBounds(1, 34, 244, 20);
		panel4.add(comboBoxSearch);
		comboBoxSearch.setBackground(Color.WHITE);
		
		
		//scroll list associates flights
		scrollPane6 = new JScrollPane();
		scrollPane6.setBounds(1, 63, 909, 226);
		panel4.add(scrollPane6);
		
		
		//list associates flights
		listFoundedFlightsAssociatesWithPlaneModel = new DefaultListModel<>();
		listSearchFlightsAssociates = new JList<>(listFoundedFlightsAssociatesWithPlaneModel);
		listSearchFlightsAssociates.setName("searchFlightsAssociatesList");
		scrollPane6.setViewportView(listSearchFlightsAssociates);
		
		
		//button search associates flights
		btnSearchAssociatesFlights = new JButton("Search associates flights");
		btnSearchAssociatesFlights.addActionListener(
				e -> searchController.findAllFlightsAssiociatesWithPlane((String)comboBoxSearch.getSelectedItem()));
		btnSearchAssociatesFlights.setBounds(593, 32, 315, 25);
		panel4.add(btnSearchAssociatesFlights);
		
		lblErrorMessageSearchPlane = new JLabel(" ");
		lblErrorMessageSearchPlane.setName("errorSearchPlaneLabel");
		lblErrorMessageSearchPlane.setForeground(Color.RED);
		lblErrorMessageSearchPlane.setBounds(12, 681, 882, 15);
		panel4.add(lblErrorMessageSearchPlane);
		
		
		//#############################################################################
		
		
		//search plane by model label
		lblPlaneModel = new JLabel("Plane model");
		lblPlaneModel.setBounds(3, 339, 98, 15);
		panel4.add(lblPlaneModel);
		
		//search plane by model text
		txtSearchModel = new JTextField();
		txtSearchModel.setName("searchModelTextBox");
		txtSearchModel.setBounds(1, 358, 585, 19);
		panel4.add(txtSearchModel);
		txtSearchModel.setColumns(10);
		
		
		//scroll list search by model
		scrollPane7 = new JScrollPane();
		scrollPane7.setBounds(1, 386, 908, 226);
		panel4.add(scrollPane7);
		
		
		//list search by model
		listFoundedPlanesByModel = new DefaultListModel<>();
		listSearchPlaneByModel = new JList<>(listFoundedPlanesByModel);
		listSearchPlaneByModel.setName("searchPlaneByModelList");
		scrollPane7.setViewportView(listSearchPlaneByModel);
		
		
		//button search by model
		btnSearchByModel = new JButton("Search by model");
		btnSearchByModel.addActionListener(
				e -> searchController.findAllPlanesByModel(txtSearchModel.getText()));
		btnSearchByModel.setBounds(593, 355, 315, 25);
		panel4.add(btnSearchByModel);
				
	}

	
	
	
	
	//######### plane methods ##########
	
	@Override
	public void showAllPlanes(List<Plane> planes) {
		planes.stream()
			.forEach(listPlanesModel::addElement);	
	}

	@Override
	public void showPlaneError(String message) {
		lblErrorMessage.setText(message);
	}

	@Override
	public void planeAdded(Plane plane) {
		listPlanesModel.addElement(plane);
		lblErrorMessage.setText(" ");
		
	}

	@Override
	public void planeRemoved(Plane plane) {
		listPlanesModel.removeElement(plane);
		lblErrorMessage.setText(" ");
	}


	
	
	//######### flight methods ##########
	
	@Override
	public void showAllFlights(List<Flight> flights) {
		flights.stream()
			.forEach(listFlightsModel::addElement);		 
	}
	
	
	@Override
	public void showFlightError(String message) { 
		lblErrorMessageFlight.setText(message);	
	}


	@Override
	public void flightAdded(Flight flight) {
		listFlightsModel.addElement(flight);
		lblErrorMessageFlight.setText(" ");
	}
 

	@Override
	public void flightRemoved(Flight flight) {
		listFlightsModel.removeElement(flight);
		lblErrorMessageFlight.setText(" ");
	}
	
	
	
	
	// ########## search methods #########
	
	@Override
	public void showSearchFlightError(String message) {
		lblErrorMessageSearch.setText(message);
	}
	
	@Override
	public void showSearchPlaneError(String message) {
		lblErrorMessageSearchPlane.setText(message);
	}
	
	@Override
	public void clearListSearchByOrigin() {
		listFoundedFlightsByOriginModel.clear();
	}
	
	@Override
	public void clearListSearchByDestination() {
		listFoundedFlightsByDestinationModel.clear();
	}
	
	@Override
	public void clearListSearchByDepartureDate() {
		listFoundedFlightsByDepartureDateModel.clear();
	}
	
	@Override
	public void clearListSearchByArrivalDate() {
		listFoundedFlightsByArrivalDateModel.clear();
	}
	
	@Override
	public void clearListSearchAssociatesFlights() {
		listFoundedFlightsAssociatesWithPlaneModel.clear();
	}
	
	@Override
	public void clearListSearchByModel() {
		listFoundedPlanesByModel.clear();	
	}
	
	@Override
	public void showAllFoundedFlightsByOrigin(List<Flight> flights) {
		listFoundedFlightsByOriginModel.clear();
		flights.stream()
			.forEach(listFoundedFlightsByOriginModel::addElement);
		lblErrorMessageSearch.setText(" ");
	}
	
	@Override
	public void showAllFoundedFlightsByDestination(List<Flight> flights) {
		listFoundedFlightsByDestinationModel.clear();
		flights.stream()
			.forEach(listFoundedFlightsByDestinationModel::addElement);
		lblErrorMessageSearch.setText(" ");
	}
	
	@Override
	public void showAllFoundedFlightsByDepartureDate(List<Flight> flights) {
		listFoundedFlightsByDepartureDateModel.clear();
		flights.stream()
			.forEach(listFoundedFlightsByDepartureDateModel::addElement);
		lblErrorMessageSearch.setText(" ");
	}
	
	@Override
	public void showAllFoundedFlightsByArrivalDate(List<Flight> flights) {
		listFoundedFlightsByArrivalDateModel.clear();
		flights.stream()
			.forEach(listFoundedFlightsByArrivalDateModel::addElement);
		lblErrorMessageSearch.setText(" ");
	}
	
	@Override
	public void showAllFoundedFlightsAssociatesWithPlane(List<Flight> flights) {
		listFoundedFlightsAssociatesWithPlaneModel.clear();
		flights.stream()
			.forEach(listFoundedFlightsAssociatesWithPlaneModel::addElement);
		lblErrorMessageSearchPlane.setText(" ");
	}
	
	@Override
	public void showAllFoundedPlanesByModel(List<Plane> planes) {
		listFoundedPlanesByModel.clear();
		planes.stream()
			.forEach(listFoundedPlanesByModel::addElement);
		lblErrorMessageSearchPlane.setText(" ");
	}
}

