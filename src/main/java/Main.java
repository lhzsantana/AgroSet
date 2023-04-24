import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.FileOutputStream;
import java.io.IOException;
public class Main {

    private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private static final GeometryJSON g = new GeometryJSON();
    private static final FeatureJSON f = new FeatureJSON();

    public static void main (String [] args) throws ParseException, IOException {

        Generator generator = new Generator();

        Polygon polygon = generateDefaultPolygon();

        FeatureCollection featureCollection = generator.fill(polygon, 10L, 2000L, 70L);

        g.writePolygon(polygon, new FileOutputStream("out-polygon.json"));
        f.writeFeatureCollection(featureCollection, new FileOutputStream("out-featurecollection.json"));
    }


    private static Polygon generateDefaultPolygon() throws ParseException {
        WKTReader reader = new WKTReader(geometryFactory);
        return (Polygon) reader.read("Polygon((-90.99071494360828  39.31417321881088, -90.98949700889592 39.31139459851116, -90.9870299103749 39.31137043611173, -90.98860697968246 39.31497054165223, -90.99071494360828 39.31417321881088))");
    }
}
