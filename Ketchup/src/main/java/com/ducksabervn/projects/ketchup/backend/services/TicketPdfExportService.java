/*******************************************************************************
 * Project Name:    Ketchup - A movie management system
 * Course:          COMP1020 - OOP and Data Structure
 * Semester:        Spring 2026
 * Members: Tran Phan Anh <25anh.tp@vinuni.edu.vn>,
 *          Nguyen Trong Khoi Nguyen <25nguyen.ntk@vinuni.edu.vn>,
 *          Nguyen Dinh Quy <25quy.nd@vinuni.edu.vn>,
 *          Hoang Duc Phat <25phat.hd@vinuni.edu.vn>,
 *          Mai Dinh Vinh <25vinh.md@vinuni.edu.vn>
 * <p>
 * Developer:       Tran Phan Anh*, Nguyen Trong Khoi Nguyen*, Nguyen Dinh Quy*,
 *                  Mai Dinh Vinh* (* equal contributions)
 * File Name:       TicketPdfExportService.java
 * Description:     Service that generates a styled PDF ticket for a given
 *                  Booking. Uses Apache PDFBox 3.x; no external fonts required.
 *******************************************************************************/

package com.ducksabervn.projects.ketchup.backend.services;

import com.ducksabervn.projects.ketchup.backend.model.Booking;
import com.ducksabervn.projects.ketchup.backend.model.Movie;
import com.ducksabervn.projects.ketchup.backend.repositories.BookingRepository;
import com.ducksabervn.projects.ketchup.backend.repositories.MovieRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Utility service that generates a PDF movie ticket for a {@link Booking}.
 *
 * <p>Call {@link #export(String, File)} with the booking ID and the desired
 * output file. The method is stateless and thread-safe.
 */
public class TicketPdfExportService {

    // A4 landscape feels more "ticket-like"; switch to LETTER if preferred.
    private static final PDRectangle PAGE_SIZE = PDRectangle.A5;

    private static final float MARGIN        = 40f;
    private static final float PAGE_WIDTH    = PAGE_SIZE.getWidth();
    private static final float PAGE_HEIGHT   = PAGE_SIZE.getHeight();

    // Colours (RGB 0–1 range)
    private static final float[] COLOR_DARK_RED  = {0.55f, 0.07f, 0.07f}; // header bg
    private static final float[] COLOR_WHITE     = {1f,    1f,    1f};
    private static final float[] COLOR_LIGHT_GRAY= {0.95f, 0.95f, 0.95f}; // row bg alt
    private static final float[] COLOR_DARK      = {0.15f, 0.15f, 0.15f}; // body text
    private static final float[] COLOR_ACCENT    = {0.55f, 0.07f, 0.07f}; // labels

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm");

    /**
     * Generates a PDF ticket for the specified booking and writes it to
     * {@code outputFile}.
     *
     * @param bookingId  UUID string of the booking to export
     * @param outputFile destination file (will be created or overwritten)
     * @throws IOException          if the file cannot be written
     * @throws IllegalArgumentException if the booking or its movie is not found
     */
    public static void export(String bookingId, File outputFile) throws IOException {
        Booking booking = BookingRepository.getBookings().get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Movie movie = MovieRepository.getMovies().get(booking.getMovieId());
        if (movie == null) {
            throw new IllegalArgumentException("Movie not found for booking: " + bookingId);
        }

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PAGE_SIZE);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                drawTicket(cs, booking, movie);
            }

            doc.save(outputFile);
        }
    }

    private static void drawTicket(PDPageContentStream cs,
                                   Booking booking,
                                   Movie   movie) throws IOException {

        PDType1Font fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDType1Font fontOblique = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        float y = PAGE_HEIGHT;
        float headerH = 80f;
        fillRect(cs, 0, y - headerH, PAGE_WIDTH, headerH, COLOR_DARK_RED);

        // App name
        drawText(cs, "KETCHUP", fontBold, 22, COLOR_WHITE,
                MARGIN, y - 32);

        // Sub-title
        drawText(cs, "Movie Ticket", fontOblique, 11, COLOR_WHITE,
                MARGIN, y - 52);

        // Booking ID (top-right, small)
        String shortId = "Ticket ID: " + booking.getBookingId().substring(0, 8).toUpperCase();
        float shortIdW = fontRegular.getStringWidth(shortId) / 1000f * 9f;
        drawText(cs, shortId, fontRegular, 9, COLOR_WHITE,
                PAGE_WIDTH - MARGIN - shortIdW, y - 52);

        y -= headerH;
        fillRect(cs, 0, 0, PAGE_WIDTH, y, COLOR_WHITE);

        y -= 24; // top padding inside body

        // Movie title
        drawText(cs, truncate(movie.getTitle(), 40), fontBold, 18, COLOR_DARK,
                MARGIN, y);
        y -= 8;

        // Thin accent underline
        setColor(cs, COLOR_DARK_RED);
        cs.addRect(MARGIN, y, PAGE_WIDTH - 2 * MARGIN, 1.5f);
        cs.fill();
        y -= 18;
        float labelX  = MARGIN;
        float valueX  = MARGIN + 130;
        float rowH    = 22f;
        boolean shade = false;

        String[][] rows = {
                {"Genre",       movie.getGenre()},
                {"Duration",    movie.getDuration() + " min"},
                {"Rating",      movie.getRating()},
                {"Showtime",    movie.getShowTime().format(DT_FMT)},
                {"Seats",       formatSeats(booking)},
                {"Total Price", "$" + booking.getTotalPrice()},
                {"Email",       booking.getBookingEmail()},
        };

        for (String[] row : rows) {
            if (shade) {
                fillRect(cs, MARGIN - 6, y - 4, PAGE_WIDTH - 2 * MARGIN + 12, rowH,
                        COLOR_LIGHT_GRAY);
            }
            drawText(cs, row[0], fontBold,    10, COLOR_ACCENT, labelX, y + 6);
            drawText(cs, row[1], fontRegular, 10, COLOR_DARK,   valueX, y + 6);
            y    -= rowH;
            shade = !shade;
        }

        y -= 14;
        drawText(cs, "Booking ID", fontBold, 8, COLOR_ACCENT, MARGIN, y);
        y -= 12;
        drawText(cs, booking.getBookingId(), fontOblique, 8, COLOR_DARK, MARGIN, y);
        y -= 18;
        drawDashedLine(cs, MARGIN, y, PAGE_WIDTH - MARGIN, y);
        y -= 14;
        drawText(cs, "Thank you for booking with Ketchup! Enjoy your movie.",
                fontOblique, 8, COLOR_DARK, MARGIN, y);
    }

    private static void fillRect(PDPageContentStream cs,
                                 float x, float y, float w, float h,
                                 float[] rgb) throws IOException {
        setColor(cs, rgb);
        cs.addRect(x, y, w, h);
        cs.fill();
    }

    private static void setColor(PDPageContentStream cs, float[] rgb) throws IOException {
        cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
    }

    private static void drawText(PDPageContentStream cs,
                                 String text,
                                 PDType1Font font,
                                 float size,
                                 float[] rgb,
                                 float x, float y) throws IOException {
        setColor(cs, rgb);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(text));
        cs.endText();
    }

    private static void drawDashedLine(PDPageContentStream cs,
                                       float x1, float y,
                                       float x2, float ignored) throws IOException {
        cs.setStrokingColor(0.7f, 0.7f, 0.7f);
        cs.setLineDashPattern(new float[]{4, 4}, 0);
        cs.setLineWidth(0.8f);
        cs.moveTo(x1, y);
        cs.lineTo(x2, y);
        cs.stroke();
        cs.setLineDashPattern(new float[]{}, 0); // reset
    }

    private static String formatSeats(Booking booking) {
        return booking.getChosenSeats().stream()
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /** Strips characters outside Latin-1 so PDType1Font won't throw. */
    private static String sanitize(String s) {
        return s == null ? "" : s.replaceAll("[^\\x00-\\xFF]", "?");
    }

    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 1) + "..." : s;
    }
}