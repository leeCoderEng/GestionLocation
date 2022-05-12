package controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import connectionManager.ConnectionManager;

import java.sql.Date;
import java.sql.PreparedStatement;

public class ReservationController {
	public enum filtre{
		Tous,
		Valide,
		Non_valide,
		Annule
	};
	public static void fetchAll (JTable table, filtre fil) {
		String query= "SELECT codeReservation, nomClient, prenomClient, dateDepReservation, dateRetReservation, isValid, isCanceled FROM reservation, client WHERE reservation.codeClient = client.codeClient";
		switch(fil) {
			case Tous:
				query += ";";
				break;
			case Valide:
				query += " AND isValid = true AND isCanceled = false;";
				break;
			case Non_valide:
				query += " AND isValid = false AND isCanceled = false;";
				break;
			case Annule:
				query += " AND isCanceled = true;";
				break;
		}
		ResultSet result = ConnectionManager.execute(query);
		
		DefaultTableModel dtm = prepareTable(table);
		
		try {
			while (result.next()) {
				String codeReserv = result.getString("codeReservation");
				String nomClient = result.getString("nomClient");
				String prenomClient = result.getString("prenomClient");
				Date dateDepart = result.getDate("dateDepReservation");
				Date dateRetour = result.getDate("dateRetReservation");
				boolean isValid = result.getBoolean("isValid");
				boolean isCanceled = result.getBoolean("isCanceled");
				Object[] row = {codeReserv, nomClient, prenomClient, dateDepart, dateRetour, isValid, isCanceled};
				
				dtm.addRow(row);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteReservation (String codeReserv) {
		try {
			PreparedStatement prepared = ConnectionManager.getConnection().prepareStatement("DELETE FROM reservation WHERE codeReservation = ?");
			prepared.setString(1, codeReserv);
			prepared.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void findReservation(String codeReservation, JTable table) {
		String query = "SELECT codeReservation, nomClient, prenomClient, dateDepReservation, dateRetReservation, isValid, isCanceled FROM reservation, client WHERE reservation.codeClient = client.codeClient AND codeReservation = ?;";
		PreparedStatement preparedSt;
		
		DefaultTableModel dtm = prepareTable(table);
		try {
			preparedSt = ConnectionManager.getConnection().prepareStatement(query);
			preparedSt.setString(1, codeReservation);
			ResultSet result = preparedSt.executeQuery();
			while (result.next()) {
				String codeReserv = result.getString("codeReservation");
				String nomClient = result.getString("nomClient");
				String prenomClient = result.getString("prenomClient");
				Date dateDepart = result.getDate("dateDepReservation");
				Date dateRetour = result.getDate("dateRetReservation");
				boolean isValid = result.getBoolean("isValid");
				boolean isCanceled = result.getBoolean("isCanceled");
				Object[] row = {codeReserv, nomClient, prenomClient, dateDepart, dateRetour, isValid, isCanceled};
				
				dtm.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static DefaultTableModel prepareTable(JTable table) {
		DefaultTableModel dtm = new DefaultTableModel();
		dtm.addColumn("Code Reservation");
		dtm.addColumn("Prenom Client");
		dtm.addColumn("Nom Client");
		dtm.addColumn("Date Depart");
		dtm.addColumn("Date Retour");
		dtm.addColumn("Valid�e");
		dtm.addColumn("Annul�e");
		dtm.setRowCount(0);
		table.setModel(dtm);
		return dtm;
	}
	
	public static boolean createReservation(String codeClient, String codeVoiture, String dateDep, String dateRet) {
		try {
			String query = "INSERT INTO `reservation` (`codeReservation`, `dateReservation`, `dateDepReservation`, `dateRetReservation`, `isValid`, `isCanceled`, `codeClient`, `codeVehicule`) VALUES (NULL, CURRENT_DATE(), ?, ?, '0', '0', ?, ?);";
			PreparedStatement prepared = ConnectionManager.getConnection().prepareStatement(query);
			prepared.setString(1, dateDep);
			prepared.setString(2, dateRet);
			prepared.setString(3, codeClient);
			prepared.setString(4, codeVoiture);
			prepared.execute();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
