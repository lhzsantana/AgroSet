
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {

    private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    Random rand = new Random();

    public DefaultFeatureCollection fill(Polygon polygon, Long distance, Long rowSpacing, Long heading) {

        Polygon boundingBox = (Polygon) polygon.getEnvelope().buffer(0.0001);

        Point point = null;

        //bottom right corner
        point = geometryFactory.createPoint(new Coordinate(
                polygon.getEnvelope().getEnvelopeInternal().getMinX(),
                polygon.getEnvelope().getEnvelopeInternal().getMinY()));

        List<Point> points = new ArrayList<>();

        int lines=0;

        do {
            List<Point> line = new ArrayList<>();

            do {
                line.add(point);

                point = nextPoint(point, distance, heading);
            } while (boundingBox.contains(point));

            points.addAll(line);

            //next line
            point = nextPointLine(polygon.getEnvelope().getEnvelopeInternal().getMinX(), polygon.getEnvelope().getEnvelopeInternal().getMaxY(), line.get(0).getX(), line.get(0).getY(), rowSpacing);
            lines++;
            //if(lines>20) break;

        } while (boundingBox.contains(point));

        return generateFeatureCollection(polygon, points);
    }

    private Point nextPoint(Point current, Long distance, Long heading) {
        double x = current.getX() + (distance * Math.cos(heading))/6378137;
        double y = current.getY() - (distance * Math.sin(heading))/6378137;

        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    private Point nextPointLine(Double minX,  Double maxY,  Double currentX, Double currentY, Long rowSpacing) {

        double x = minX;
        double y = currentY + (rowSpacing)/6378137.0;

        // overwrite if it is bigger, start going right
        if(y>maxY) {
            x = currentX + (rowSpacing)/6378137.0;
            y = maxY;
        }


        System.out.println("Next line point: "+x+", "+y);

        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    private DefaultFeatureCollection generateFeatureCollection(Polygon polygon, List<Point> points) {

        Instant s = Instant.now();


        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("points");
        simpleFeatureTypeBuilder.add("geometry", Point.class);
        simpleFeatureTypeBuilder.add("timestamp", Instant.class);
        simpleFeatureTypeBuilder.add("seedRate", Double.class);
        simpleFeatureTypeBuilder.add("elevation", Double.class);
        simpleFeatureTypeBuilder.add("crop", String.class);
        simpleFeatureTypeBuilder.add("seedDepth", Double.class);
        simpleFeatureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);

        DefaultFeatureCollection simpleFeatureCollection = new DefaultFeatureCollection();

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(simpleFeatureTypeBuilder.buildFeatureType());

        for(Point p: points) {
            if(polygon.contains(p)) {
                s = s.plusSeconds(1);

                builder.add(p);
                SimpleFeature feature = builder.buildFeature( null);
                feature.setAttribute("timestamp", s);
                feature.setAttribute("seedRate", rand.nextDouble());
                feature.setAttribute("elevation", rand.nextDouble());
                feature.setAttribute("crop", "soybeans");
                feature.setAttribute("seedDepth",rand.nextDouble());

                simpleFeatureCollection.add(feature);
            }
        }

        return simpleFeatureCollection;
    }
}
