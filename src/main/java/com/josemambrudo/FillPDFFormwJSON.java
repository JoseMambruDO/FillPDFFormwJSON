package com.josemambrudo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;


import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;


@SpringBootApplication
public class FillPDFFormwJSON implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(FillPDFFormwJSON.class);

	private static File outPDFFile=null;
	private static File tempPDFFile=null;
	private static File dataJsonFile=null;
	private static File outPDFnoPass=null;
	private String userPass;

	private void getInitValues(String... args) throws Exception {
		Validation val = new Validation();

		for (int i = 0; i < args.length; ++i) {
			String varg = args[i];

			if (i + 1 < args.length)
				switch (varg) {
				case "-tempPDFFile":
					tempPDFFile = val.validateFile(args[i + 1], FileExtension.PDF);
					break;
				case "-outPDFFile":
					outPDFFile = new File(val.validateUserPass(args[i + 1]));
					break;
				case "-outPDFnoPass":
					outPDFnoPass = new File(val.validateUserPass(args[i + 1]));
					break;
				case "-dataJsonFile":
					dataJsonFile = val.validateFile(args[i + 1], FileExtension.JSON);
					break;
				case "-sk":
					userPass = val.validateUserPass(args[i + 1]);
					break;

				}
		}

	}

	public static void main(String[] args) {

		LocalDateTime initTime = LocalDateTime.now();
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(FillPDFFormwJSON.class, args);
		LOG.info("APPLICATION FINISHED");

		LocalDateTime finalTime = LocalDateTime.now();
		Duration diff = Duration.between(initTime, finalTime);
		LOG.info("DURATION FINISHED: " + diff.toMinutes() + "min. " + diff.getSeconds() + "sec");

	}

	@Override
	public void run(String... args) throws FileNotFoundException {

		try {
			LOG.info("Get init parameters");
			getInitValues(args);

			LOG.info("Fill form template");
			fillFormTemplate(tempPDFFile, dataJsonFile, outPDFFile,outPDFnoPass, userPass);

			LOG.info("Adding Success file");
			com.josemambrudo.Util.resultProcess(outPDFFile, "SUCCESS");

		} catch (Exception e) {
			LOG.error("Unfortunately, an error has occurred.");
			e.printStackTrace();
			com.josemambrudo.Util.resultProcess(outPDFFile, e.toString());
		}
	}

	private void fillFormTemplate(File tempPDF, File dataJsonFile2, File outPDFFile2, File outPDFnoPass, String userpass)
			throws FileNotFoundException, IOException, ParseException {

		LOG.info("Loading value from json file.");
		JSONObject joValues = Util.getJSONObject(dataJsonFile2);

		LOG.info("Loading pdf template");
		PDDocument pdfDocument = PDDocument.load(tempPDF);

		PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
		
		
		
		LOG.info("Setting values to Form.");
		if (acroForm != null)
			for (PDField pdfField : acroForm.getFields()) {
				String pn = pdfField.getPartialName();
				
				PDTextField field = (PDTextField) acroForm.getField(pn);
				field.setReadOnly(true);
				field.setDefaultAppearance("/Helv 10 Tf 0 g");
				
				if (pn.startsWith("FR")) {
					String valueFromJson = (String) joValues.get(pn);
					field.setValue(valueFromJson);
				}
			}
		
		if (outPDFnoPass != null) {
			LOG.info("Save outPDFnoPass without pass");
			pdfDocument.save(outPDFnoPass);
		}
		
		LOG.info("Asing permision to new pdf Document");
		pdfDocument.protect(generatePermision(UUID.randomUUID().toString(), userpass));

		LOG.info("Save and close new file");

		
		Util.verifyDelete(outPDFFile2);
		pdfDocument.save(outPDFFile2);
		
		pdfDocument.close();

	}

	private StandardProtectionPolicy generatePermision(String owner, String userPass) {
		AccessPermission ap = new AccessPermission();

		ap.setCanModify(false);
		ap.setCanExtractContent(false);
		ap.setCanPrint(false);
		ap.setReadOnly();

		ap.setCanFillInForm(false);
		ap.setCanModifyAnnotations(false);
		ap.setCanAssembleDocument(false);

		StandardProtectionPolicy sp = new StandardProtectionPolicy(owner, userPass, ap);
		sp.setEncryptionKeyLength(128);

		return sp;

	}

}
