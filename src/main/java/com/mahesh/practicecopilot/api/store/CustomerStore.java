package com.mahesh.practicecopilot.api.store;

import com.mahesh.practicecopilot.api.models.CustomerResourceBean;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Singleton store that treats customers.csv as the persistence layer.
 * Loads data from the classpath resource on first access and
 * mirrors every mutation back to the same file on disk.
 */
public class CustomerStore {

    private static final String CSV_RESOURCE = "/customers.csv";
    private static final String CSV_HEADER   = "id,name,email,phone,address,createdDate";

    private static final CustomerStore INSTANCE = new CustomerStore();

    private final Path csvPath;
    private final Map<Long, CustomerResourceBean> store = new LinkedHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    private CustomerStore() {
        URL url = getClass().getResource(CSV_RESOURCE);
        if (url == null) {
            throw new IllegalStateException("customers.csv not found on classpath");
        }
        try {
            csvPath = Paths.get(url.toURI());
            loadFromDisk();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise CustomerStore", e);
        }
    }

    public static CustomerStore getInstance() {
        return INSTANCE;
    }

    // ── CRUD operations ─────────────────────────────────────────────────────

    public List<CustomerResourceBean> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<CustomerResourceBean> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public CustomerResourceBean create(CustomerResourceBean bean) {
        if (bean.getId() == null) {
            throw new IllegalArgumentException("id is mandatory");
        }

        long id = bean.getId();
        if (store.containsKey(id)) {
            throw new IllegalArgumentException("Customer already exists with id: " + id);
        }

        // Ignore request.createdDate and always set server-side creation timestamp.
        CustomerResourceBean toStore = new CustomerResourceBean(
            id,
            bean.getName(),
            bean.getEmail(),
            bean.getPhone(),
            bean.getAddress(),
            LocalDateTime.now()
        );
        store.put(id, toStore);
        if (id > idSequence.get()) {
            idSequence.set(id);
        }
        saveToDisk();
        return toStore;
    }

    public Optional<CustomerResourceBean> update(Long id, CustomerResourceBean updated) {
        if (!store.containsKey(id)) {
            return Optional.empty();
        }
        CustomerResourceBean existing = store.get(id);
        LocalDateTime createdDate =
            (existing != null && existing.getCreatedDate() != null)
                ? existing.getCreatedDate()
                : LocalDateTime.now();

        // Ignore request.createdDate and keep/pin a non-null server-side creation time.
        CustomerResourceBean toStore = new CustomerResourceBean(
            id,
            updated.getName(),
            updated.getEmail(),
            updated.getPhone(),
            updated.getAddress(),
            createdDate
        );

        store.put(id, toStore);
        saveToDisk();
        return Optional.of(toStore);
    }

    public boolean delete(Long id) {
        boolean removed = store.remove(id) != null;
        if (removed) saveToDisk();
        return removed;
    }

    // ── CSV I/O ──────────────────────────────────────────────────────────────

    private void loadFromDisk() throws IOException {
        List<String> lines = Files.readAllLines(csvPath);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            CustomerResourceBean c = parseLine(line);
            store.put(c.getId(), c);
            if (c.getId() > idSequence.get()) {
                idSequence.set(c.getId());
            }
        }
    }

    private void saveToDisk() {
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER);
        lines.addAll(
            store.values().stream()
                .map(this::toLine)
                .collect(Collectors.toList())
        );
        try {
            Files.write(csvPath, lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist customers.csv", e);
        }
    }

    private CustomerResourceBean parseLine(String line) {
        String[] p = line.split(",", -1);
        LocalDateTime createdDate =
            (p.length > 5 && !p[5].trim().isEmpty())
                ? LocalDateTime.parse(p[5].trim())
                : LocalDateTime.now();

        return new CustomerResourceBean(
            Long.parseLong(p[0].trim()),
            p[1].trim(),
            p[2].trim(),
            p[3].trim(),
            p[4].trim(),
            createdDate
        );
    }

    private String toLine(CustomerResourceBean c) {
        return String.join(",",
            String.valueOf(c.getId()),
            nvl(c.getName()),
            nvl(c.getEmail()),
            nvl(c.getPhone()),
            nvl(c.getAddress()),
            c.getCreatedDate() != null ? c.getCreatedDate().toString() : ""
        );
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }
}
