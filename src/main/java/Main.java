import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class Main {

    private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    public static void main (String [] args) throws Exception {

        String type = "interpolation";

        long start = System.currentTimeMillis();

        Generator generator = new Generator();

        Polygon polygon = generateDefaultPolygon();

        generator.fill(type, polygon, 10L, 2000L, 70L);

        long end = System.currentTimeMillis();

        System.out.println("Total time : " + (end-start) + " ms.");

    }

    private static Polygon generateDefaultPolygon() throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        return (Polygon) reader.read("Polygon((-90.99071494360828  39.31417321881088, -90.98949700889592 39.31139459851116, -90.9870299103749 39.31137043611173, -90.98860697968246 39.31497054165223, -90.99071494360828 39.31417321881088))");
    }

}
