package cz.cvut.fel.nss.tour.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import cz.cvut.fel.nss.tour.entity.*;
import org.springframework.cache.CacheManager;
import java.time.LocalDate;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName("tour-service-cache");

        config.addMapConfig(new MapConfig()
                .setName("tours")
                .setTimeToLiveSeconds(300)
                .setEvictionConfig(new EvictionConfig()
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
                        .setSize(1000)));

        config.addMapConfig(new MapConfig()
                .setName("toursByDate")
                .setTimeToLiveSeconds(300)
                .setEvictionConfig(new EvictionConfig()
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setMaxSizePolicy(MaxSizePolicy.PER_NODE)
                        .setSize(1000)));

        config.getSerializationConfig().getCompactSerializationConfig()
                .addSerializer(new TourSerializer())
                .addSerializer(new TripSerializer())
                .addSerializer(new ActivitySerializer());

        return Hazelcast.getOrCreateHazelcastInstance(config);
    }

    private static class TourSerializer implements CompactSerializer<Tour> {
        @Override
        public Tour read(com.hazelcast.nio.serialization.compact.CompactReader reader) {
            Tour tour = new Tour();
            tour.setId(reader.readNullableInt64("id"));
            tour.setTitle(reader.readString("title"));
            tour.setDestination(reader.readString("destination"));
            tour.setStartDate(reader.readNullableInt32("startDate") != null ? LocalDate.ofEpochDay(reader.readNullableInt32("startDate")) : null);
            tour.setEndDate(reader.readNullableInt32("endDate") != null ? LocalDate.ofEpochDay(reader.readNullableInt32("endDate")) : null);
            tour.setStatus(TourStatus.valueOf(reader.readString("status")));
            tour.setDescription(reader.readString("description"));
            tour.setCapacity(reader.readInt32("capacity"));
            tour.setPrice(reader.readFloat64("price"));
            Long[] accIds = reader.readArrayOfNullableInt64("accommodationsId");
            if (accIds != null) {
                tour.setAccommodationsId(java.util.Arrays.asList(accIds));
            }
            Activity[] activities = reader.readArrayOfCompact("activities", Activity.class);
            if (activities != null) {
                tour.setActivities(java.util.Arrays.asList(activities));
            }
            Trip[] trips = reader.readArrayOfCompact("trips", Trip.class);
            if (trips != null) {
                tour.setTrips(java.util.Arrays.asList(trips));
            }
            return tour;
        }

        @Override
        public void write(com.hazelcast.nio.serialization.compact.CompactWriter writer, Tour tour) {
            writer.writeNullableInt64("id", tour.getId());
            writer.writeString("title", tour.getTitle());
            writer.writeString("destination", tour.getDestination());
            writer.writeNullableInt32("startDate", tour.getStartDate() != null ? (int) tour.getStartDate().toEpochDay() : null);
            writer.writeNullableInt32("endDate", tour.getEndDate() != null ? (int) tour.getEndDate().toEpochDay() : null);
            writer.writeString("status", tour.getStatus().name());
            writer.writeString("description", tour.getDescription());
            writer.writeInt32("capacity", tour.getCapacity());
            writer.writeFloat64("price", tour.getPrice());
            writer.writeArrayOfNullableInt64("accommodationsId", tour.getAccommodationsId() != null ? tour.getAccommodationsId().toArray(new Long[0]) : null);
            writer.writeArrayOfCompact("activities", tour.getActivities() != null ? tour.getActivities().toArray(new Activity[0]) : null);
            writer.writeArrayOfCompact("trips", tour.getTrips() != null ? tour.getTrips().toArray(new Trip[0]) : null);
        }

        @Override
        public String getTypeName() {
            return "Tour";
        }

        @Override
        public Class<Tour> getCompactClass() {
            return Tour.class;
        }
    }

    private static class TripSerializer implements CompactSerializer<Trip> {
        @Override
        public Trip read(com.hazelcast.nio.serialization.compact.CompactReader reader) {
            Trip trip = new Trip();
            trip.setId(reader.readNullableInt64("id"));
            trip.setCarrier(reader.readString("carrier"));
            trip.setDepartAt(reader.readNullableInt64("departAt") != null ? java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(reader.readNullableInt64("departAt")), java.time.ZoneId.systemDefault()) : null);
            trip.setArriveAt(reader.readNullableInt64("arriveAt") != null ? java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(reader.readNullableInt64("arriveAt")), java.time.ZoneId.systemDefault()) : null);
            trip.setFrom(reader.readString("from"));
            trip.setTo(reader.readString("to"));
            trip.setType(TransportType.valueOf(reader.readString("type")));
            return trip;
        }

        @Override
        public void write(com.hazelcast.nio.serialization.compact.CompactWriter writer, Trip trip) {
            writer.writeNullableInt64("id", trip.getId());
            writer.writeString("carrier", trip.getCarrier());
            writer.writeNullableInt64("departAt", trip.getDepartAt() != null ? trip.getDepartAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
            writer.writeNullableInt64("arriveAt", trip.getArriveAt() != null ? trip.getArriveAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
            writer.writeString("from", trip.getFrom());
            writer.writeString("to", trip.getTo());
            writer.writeString("type", trip.getType().name());
            // Ignore tour to break recursion
        }

        @Override
        public String getTypeName() {
            return "Trip";
        }

        @Override
        public Class<Trip> getCompactClass() {
            return Trip.class;
        }
    }

    private static class ActivitySerializer implements CompactSerializer<Activity> {
        @Override
        public Activity read(com.hazelcast.nio.serialization.compact.CompactReader reader) {
            Activity activity = new Activity();
            activity.setId(reader.readNullableInt64("id"));
            activity.setName(reader.readString("name"));
            activity.setDescription(reader.readString("description"));
            activity.setDuration(reader.readInt32("duration"));
            activity.setPrice(reader.readFloat64("price"));
            activity.setStart(reader.readNullableInt64("start") != null ? java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(reader.readNullableInt64("start")), java.time.ZoneId.systemDefault()) : null);
            activity.setEnd(reader.readNullableInt64("end") != null ? java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(reader.readNullableInt64("end")), java.time.ZoneId.systemDefault()) : null);
            return activity;
        }

        @Override
        public void write(com.hazelcast.nio.serialization.compact.CompactWriter writer, Activity activity) {
            writer.writeNullableInt64("id", activity.getId());
            writer.writeString("name", activity.getName());
            writer.writeString("description", activity.getDescription());
            writer.writeInt32("duration", activity.getDuration());
            writer.writeFloat64("price", activity.getPrice());
            writer.writeNullableInt64("start", activity.getStart() != null ? activity.getStart().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
            writer.writeNullableInt64("end", activity.getEnd() != null ? activity.getEnd().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
            // Ignore tours to break recursion
        }

        @Override
        public String getTypeName() {
            return "Activity";
        }

        @Override
        public Class<Activity> getCompactClass() {
            return Activity.class;
        }
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

}
