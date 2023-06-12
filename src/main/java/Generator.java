
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.opengis.feature.simple.SimpleFeature;

import java.io.FileOutputStream;
import java.sql.Driver;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import javax.sql.DataSource;

public class Generator {

    private static final GeometryJSON g = new GeometryJSON(20);
    private static final FeatureJSON f = new FeatureJSON(g);

    public void fill(String type, Polygon polygon, Long distance, Long rowSpacing, Long heading) throws Exception {

        switch (type) {
            case "rowsLines":
                g.writePolygon(polygon, new FileOutputStream("out-polygon-rows-lines.json"));
                f.writeFeatureCollection(GeneratorRowsLines.generateRowsLines(polygon, distance, rowSpacing, heading), new FileOutputStream("out-featurecollection"+type+".json"));
                break;
            case "interpolation":
                g.writePolygon(polygon, new FileOutputStream("out-polygon-interpolation.json"));
                f.writeFeatureCollection(GeneratorInterpolation.generateInterpolation(polygon, distance, rowSpacing), new FileOutputStream("out-featurecollection"+type+".json"));
                break;
            case "walk":
                g.writePolygon(polygon, new FileOutputStream("out-polygon-random-walking.json"));
                f.writeFeatureCollection(GeneratorRandomWalking.generateRandomWalking(polygon), new FileOutputStream("out-featurecollection"+type+".json"));
                break;
            default:
                throw new Exception("Wrong type");
        }
    }
}
