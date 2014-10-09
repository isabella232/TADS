package com.telestax.tads2014;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * <p>
 * A simple servlet taking advantage of features added in 3.0.
 * </p>
 * 
 * <p>
 * The servlet is registered and mapped to /TADSPoll using the {@linkplain WebServlet
 * @HttpServlet}. The {@link TADSPollService} is injected by CDI.
 * </p>
 * 
 * @author Jean Deruelle
 * 
 */
@SuppressWarnings("serial")
@WebServlet("/TADSPoll")
public class TADSPollServlet extends HttpServlet {

	@Inject
	TADSPollService tadsPollService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String fromNumber = req.getParameter("from");
		String toNumber = req.getParameter("to");
		String drink = req.getParameter("drink");

		String birthDate = tadsPollService.getBirthDate(toNumber);
		String location = "France";
		PhoneNumberInformation phoneNumberInformation = null;
		try {
			phoneNumberInformation = tadsPollService
					.getPhoneNumberInformation(fromNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String favDrink = tadsPollService.getDrink(drink);
		location = phoneNumberInformation.getLocation();

		System.out.println("from " + fromNumber + ", to " + toNumber
				+ ", drink " + drink);
		System.out.println("birthDate " + birthDate + ", location " + location
				+ ", favDrink " + favDrink);

		TADS2014Response tads2014Response = new TADS2014Response(birthDate,
				favDrink, phoneNumberInformation);

		try {
			boolean updated = tadsPollService.updateDashboard(tads2014Response);
			System.out.println("udpated ? " + updated);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.setContentType("application/json");
		PrintWriter writer = resp.getWriter();
		Gson gson = new Gson();
		writer.println(gson.toJson(tads2014Response));
		writer.close();
	}

}
