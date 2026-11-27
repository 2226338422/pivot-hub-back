package com.pivothub.business.map.typhoon.utils;

import lombok.Value;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * 判断台风的未来预测点及相邻线段是否与省界多边形相交。
 */
public final class TyphoonForecastMatcher {
    private TyphoonForecastMatcher() {
    }

    @Value
    public static class Point {
        double longitude;
        double latitude;
    }

    @Value
    public static class TimedPoint {
        Point point;
        Instant time;
    }

    public static final class Boundary {
        private final List<Ring> rings;

        public Boundary(List<List<Point>> outlines) {
            rings = outlines.stream().map(Ring::new).toList();
        }

        private boolean intersects(Point from, Point to) {
            return rings.stream().anyMatch(ring -> ring.intersects(from, to));
        }
    }

    private static final class Ring {
        private final List<Point> outline;
        private final Path2D.Double polygon;
        private final Rectangle2D bounds;

        private Ring(List<Point> outline) {
            this.outline = List.copyOf(outline);
            polygon = new Path2D.Double();
            Point first = outline.get(0);
            polygon.moveTo(first.getLongitude(), first.getLatitude());
            outline.stream().skip(1).forEach(point -> polygon.lineTo(point.getLongitude(), point.getLatitude()));
            polygon.closePath();
            bounds = polygon.getBounds2D();
        }

        private boolean intersects(Point from, Point to) {
            if (!bounds.intersectsLine(from.getLongitude(), from.getLatitude(), to.getLongitude(), to.getLatitude())) {
                return false;
            }
            if (polygon.contains(from.getLongitude(), from.getLatitude())
                    || polygon.contains(to.getLongitude(), to.getLatitude())) {
                return true;
            }
            Point previous = outline.get(outline.size() - 1);
            for (Point current : outline) {
                if (!previous.equals(current) && Line2D.linesIntersect(
                        from.getLongitude(), from.getLatitude(), to.getLongitude(), to.getLatitude(),
                        previous.getLongitude(), previous.getLatitude(), current.getLongitude(), current.getLatitude())) {
                    return true;
                }
                previous = current;
            }
            return false;
        }
    }

    /**
     * @Description: 判断起算时刻之后的预报路径是否与省界相交
     * @param path 按时刻升序排列的同坐标系预报路径
     * @param boundary 同坐标系省级行政区边界
     * @param now 预报路径的起算时刻
     * @return 预报路径是否与省界相交
     */
    public static boolean match(List<TimedPoint> path, Boundary boundary, Instant now) {
        TimedPoint previous = null;
        for (TimedPoint current : path) {
            if (!current.getTime().isBefore(now) && (intersects(previous, current, boundary, now)
                    || boundary.intersects(current.getPoint(), current.getPoint()))) {
                return true;
            }
            previous = current;
        }
        return false;
    }

    private static boolean intersects(TimedPoint previous, TimedPoint current, Boundary boundary, Instant now) {
        if (previous == null || !current.getTime().isAfter(previous.getTime())) {
            return false;
        }
        Point from = previous.getPoint();
        Point to = current.getPoint();
        if (from.getLongitude() - to.getLongitude() > 180) {
            to = new Point(to.getLongitude() + 360, to.getLatitude());
        } else if (to.getLongitude() - from.getLongitude() > 180) {
            from = new Point(from.getLongitude() + 360, from.getLatitude());
        }
        if (previous.getTime().isBefore(now)) {
            double fraction = (double) Duration.between(previous.getTime(), now).toNanos()
                    / Duration.between(previous.getTime(), current.getTime()).toNanos();
            from = new Point(from.getLongitude() + (to.getLongitude() - from.getLongitude()) * fraction,
                    from.getLatitude() + (to.getLatitude() - from.getLatitude()) * fraction);
        }
        return boundary.intersects(from, to);
    }
}
