package de.streberpower.webuntisapi2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisDate;
import de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes.WebUntisTime;
import de.streberpower.webuntisapi2.WebUntisObjects.ClassUnit;
import de.streberpower.webuntisapi2.WebUntisObjects.DayTimeGridUnits;
import de.streberpower.webuntisapi2.WebUntisObjects.SchoolDay;
import de.streberpower.webuntisapi2.WebUntisObjects.Timetable;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Streberpower on 11.10.2015.
 */
public class JTimetable extends Application {
    public static final String SESSION_CONFIGURATION_FILE = "session_conf.json";
    private static final double WEEKDAY_HEIGHT = 50d;
    private static Logger logger = LoggerFactory.getLogger(JTimetable.class);
    private Timetable timetable;
    private ExecutorService executorService;
    private ResizableCanvas canvas;
    private boolean needsRedraw = true;
    private Gson gson;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        canvas = new ResizableCanvas();
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(canvas);
        canvas.widthProperty().bind(
                stackPane.widthProperty());
        canvas.heightProperty().bind(
                stackPane.heightProperty());
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.show();
        gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.STATIC).create();
        executorService = Executors.newCachedThreadPool();
        draw(canvas);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(SESSION_CONFIGURATION_FILE);
                    SessionConfiguration conf = SessionConfiguration.fromFile(gson, file);

                    Session session = new Session(conf);

                    session.login();

                    WebUntisDate from = new WebUntisDate();
                    WebUntisDate to = new WebUntisDate();
                    to.add(Calendar.DAY_OF_YEAR, 7);

                    Timetable t = session.getTimetable(from, to);
                    session.logout();
                    logger.debug(session.gson.toJson(t));
                    timetable = t;
                } catch (WebUntisConnectionException | WebUntisParseException e) {
                    e.printStackTrace();
                }
            }
        });
        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(canvas);
            }
        };
        timer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                executorService.shutdown();
                try {
                    Request.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }));
    }

    private void draw(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (timetable == null) {
            clear(canvas, gc);
            drawLoading(canvas, gc);
        } else {
            if (needsRedraw) {
                clear(canvas, gc);
                drawTimetable(canvas, gc, timetable);
            }
            needsRedraw = false;
        }
    }

    private void clear(Canvas canvas, GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    private void drawTimetable(Canvas canvas, GraphicsContext gc, Timetable timetable) {
        final double heightOffset = WEEKDAY_HEIGHT;
        final double height = canvas.getHeight() - heightOffset;
        List<DayTimeGridUnits> timegridUnits = timetable.timeGridUnits;
        final double columnSize = ((1 / (timegridUnits.size() + 1d)) * canvas.getWidth());
        final double widthOffset = columnSize;
        final double width = canvas.getWidth() - widthOffset;
        gc.setLineWidth(1d);
        gc.setStroke(Color.DIMGREY);
        gc.setFont(Font.font(WEEKDAY_HEIGHT / 1.5d));
        for (int i = 0; i < timegridUnits.size(); i++) {
            double offset = (i / (double) timegridUnits.size()) * width + widthOffset;
            gc.strokeLine(offset, 0d, offset, canvas.getHeight());
            gc.fillText(timegridUnits.get(i).day.name(), offset + columnSize / 2d, WEEKDAY_HEIGHT / 2d);
        }
        //gc.strokeLine(0d, WEEKDAY_HEIGHT, canvas.getWidth(), WEEKDAY_HEIGHT);
        for (int i = 0; i < timegridUnits.get(0).timeUnits.size(); i++) {
            double offset = (i / (double) timegridUnits.get(0).timeUnits.size()) * height + heightOffset;
            gc.strokeLine(0d, offset, canvas.getWidth(), offset);
            gc.fillText(timegridUnits.get(0).timeUnits.get(i).startTime.format(":") + " - " +
                    timegridUnits.get(0).timeUnits.get(i).endTime.format(":"), columnSize / 2d, offset
                    + (0.5d / ((double) timegridUnits.get(0).timeUnits.size() + 1d)) * canvas.getHeight());
        }
        for (SchoolDay day : timetable.days) {
            int offset = 0;
            DayTimeGridUnits.DayTimeGridDays weekday = DayTimeGridUnits.DayTimeGridDays.fromCalendarInt(day.date.get(Calendar.DAY_OF_WEEK));
            if (timetable.hasDayInTimeGrid(weekday)) {
                while (weekday != timegridUnits.get(offset).day) {
                    offset++;
                    if (offset >= timegridUnits.size()) break;
                }
                double x = offset * columnSize + widthOffset;
                if (day.holiday != null) {
                    gc.save();
                    gc.setFill(Color.BEIGE);
                    gc.fillRect(x, heightOffset, x + columnSize, canvas.getHeight());
                    gc.rotate(90d);
                    gc.setFill(Color.CRIMSON);
                    gc.setFont(Font.font(WEEKDAY_HEIGHT));
                    gc.fillText(day.holiday.name, x + columnSize / 2d, widthOffset + width / 2d);
                    gc.restore();
                }
                long startTime = timegridUnits.get(offset).getDayStart().getTimeInMillis();
                long endTime = timegridUnits.get(offset).getDayEnd().getTimeInMillis();
                long deltaTime = endTime - startTime;
                for (ClassUnit unit : day.units) {
                    if (unit.code == ClassUnit.ClassUnitCode.CANCELLED) continue;
                    double x1 = x;
                    double x2 = x1 + columnSize;
                    double y1 = ((unit.startTime.getTimeInMillis() - startTime) / (double) deltaTime) * height + heightOffset;
                    double y2 = ((unit.endTime.getTimeInMillis() - startTime) / (double) deltaTime) * height + heightOffset;
                    //System.out.printf("startTime=%d\tendTime=%d\tstartTimeA=%d\tdeltaTime=%d\n", unit.startTime.getTimeInMillis(), unit.endTime.getTimeInMillis(), startTime, deltaTime);
                    //System.out.printf("x1=%.2f\tx2=%.2f\ty1=%.2f\ty2=%.2f\n", x1, x2, y1, y2);
                    gc.save();
                    gc.setFill(unit.subjects.get(0).backColor);
                    gc.fillRoundRect(x1, y1, x2 - x1, y2 - y1, 25d, 25d);
                    gc.setFill(unit.subjects.get(0).foreColor);
                    gc.setFont(Font.font(20d));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setTextBaseline(VPos.CENTER);
                    gc.fillText(unit.subjects.get(0).longName, (x2 - x1) / 2d + x1, (y2 - y1) / 2d + y1);
                    gc.restore();
                }
            }
        }
    }

    private void drawLoading(Canvas canvas, GraphicsContext gc) {
        gc.setFill(Color.CRIMSON);
        gc.setFont(Font.font(50d));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        final long time = System.currentTimeMillis();
        String loadingText = "Loading";
        for (int i = 0; i < (time % 2000) / 500; i++) {
            loadingText += ".";
        }
        gc.fillText(loadingText, canvas.getWidth() / 2d, canvas.getHeight() / 2d);
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    class ResizableCanvas extends Canvas {

        public ResizableCanvas() {
            // Redraw canvas when size changes.
            ChangeListener<Number> listener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    JTimetable.this.needsRedraw = true;
                }
            };
            widthProperty().addListener(listener);
            heightProperty().addListener(listener);
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }
}
