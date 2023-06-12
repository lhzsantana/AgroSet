
import org.apache.commons.lang3.RandomStringUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.opengis.feature.simple.SimpleFeature;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratorRandomWalking {

    private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    static Random rand = new Random();

    private static Point nextPoint(Point current, Long distance, Long heading) {

        double x = current.getX() + (distance * Math.cos(heading))/6378137;
        double y = current.getY() - (distance * Math.sin(heading))/6378137;

        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    public static DefaultFeatureCollection generateRandomWalking(Polygon polygon){

        Polygon boundingBox = (Polygon) polygon.getEnvelope().buffer(0.0001);

        int limit = rand.nextInt(100);
        long distance, heading;

        //bottom left corner
        Point point = geometryFactory.createPoint(new Coordinate(
                polygon.getEnvelope().getEnvelopeInternal().getMinX(),
                polygon.getEnvelope().getEnvelopeInternal().getMinY()));

        //top right corner
        Point pointTL = geometryFactory.createPoint(new Coordinate(
                polygon.getEnvelope().getEnvelopeInternal().getMaxX(),
                polygon.getEnvelope().getEnvelopeInternal().getMaxY()));

        long maxDistance = Math.abs(Math.round(point.distance(pointTL)*637811));

        List<Point> points = new ArrayList<>();

        do {
            do {
                distance = rand.nextInt((int) maxDistance);
                heading = rand.nextInt(360);

                point = nextPoint(point, distance, heading);
            } while(!polygon.contains(point));

            points.add(point);

        } while (points.size()<limit);

        System.out.println("Number of points generated: "+ points.size());

        return generateFeatureCollection(polygon, points, distance);
    }

    private static DefaultFeatureCollection generateFeatureCollection(Polygon polygon, List<Point> points, Long distance) {

        Instant s = Instant.now();

        SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName("points");
        simpleFeatureTypeBuilder.add("geometry", Point.class);
        simpleFeatureTypeBuilder.add("timestamp", Instant.class);
        simpleFeatureTypeBuilder.add("operator", String.class);
        simpleFeatureTypeBuilder.add("comments", String.class);
        simpleFeatureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);

        DefaultFeatureCollection simpleFeatureCollection = new DefaultFeatureCollection();

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(simpleFeatureTypeBuilder.buildFeatureType());

        for(Point p: points) {

            GeometricShapeFactory shape = new GeometricShapeFactory(geometryFactory);
            shape.setCentre(p.getCoordinate());
            shape.setSize(distance);
            shape.setNumPoints(10);

            s = s.plusSeconds(1);

            builder.add(shape.createCircle());
            SimpleFeature feature = builder.buildFeature( null);
            feature.setAttribute("timestamp", s);
            feature.setAttribute("operator", RandomStringUtils.randomAlphabetic(10));
            feature.setAttribute("comments", RandomStringUtils.randomAlphabetic(100));

            simpleFeatureCollection.add(feature);
        }

        return simpleFeatureCollection;
    }

}
