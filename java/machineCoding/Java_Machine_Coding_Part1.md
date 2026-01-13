# Java Machine Coding Round - Interview Questions & Solutions

> **Target**: SDE2/SDE3 positions  
> **Duration**: Typically 60-90 minutes per question  
> **Focus**: Production-ready code with proper OOP design

---

## Table of Contents

1. [Parking Lot System](#1-parking-lot-system)
2. [LRU Cache](#2-lru-cache)
3. [Tic-Tac-Toe Game](#3-tic-tac-toe-game)
4. [Meeting Scheduler](#4-meeting-scheduler)
5. [Snake and Ladder Game](#5-snake-and-ladder-game)

---

## General Interview Checklist

Before starting any machine coding round:

### ✅ Requirements Clarification (5 minutes)
- [ ] Ask about scale (users, requests per second)
- [ ] Clarify functional requirements
- [ ] Identify edge cases
- [ ] Ask about non-functional requirements (thread-safety, persistence)

### ✅ Design Discussion (10 minutes)
- [ ] Sketch class diagram
- [ ] Identify key entities and relationships
- [ ] Discuss design patterns to use
- [ ] Get interviewer buy-in

### ✅ Implementation (40-50 minutes)
- [ ] Start with interfaces and abstractions
- [ ] Implement core functionality first
- [ ] Add validation and error handling
- [ ] Write clean, readable code

### ✅ Testing & Edge Cases (10 minutes)
- [ ] Write test cases
- [ ] Demonstrate working code
- [ ] Discuss edge cases handled
- [ ] Mention additional features

---

## 1. Parking Lot System

### Problem Statement
Design a parking lot system that can:
- Park vehicles of different types (Car, Bike, Truck)
- Different parking spot sizes (Small, Medium, Large)
- Track available spots
- Calculate parking fees based on duration

### Interview Checklist

**Requirements:**
- [ ] Clarify vehicle types supported
- [ ] Ask about multiple floors/levels
- [ ] Pricing strategy (hourly, daily)
- [ ] Entry/exit gates handling
- [ ] Thread safety required?

**Design Decisions:**
- [ ] Strategy pattern for pricing
- [ ] Factory pattern for vehicle creation
- [ ] Singleton for ParkingLot
- [ ] Enum for spot types

**Implementation Focus:**
- [ ] Clear separation of concerns
- [ ] Immutable Ticket objects
- [ ] Proper encapsulation
- [ ] Clean error handling

### Solution

```java
// Enums
enum VehicleType {
    BIKE, CAR, TRUCK
}

enum SpotSize {
    SMALL, MEDIUM, LARGE
}

enum SpotStatus {
    AVAILABLE, OCCUPIED
}

// Value Objects
class Ticket {
    private final String ticketId;
    private final String vehicleNumber;
    private final VehicleType vehicleType;
    private final int spotNumber;
    private final LocalDateTime entryTime;
    
    public Ticket(String ticketId, String vehicleNumber, VehicleType vehicleType, 
                  int spotNumber, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.spotNumber = spotNumber;
        this.entryTime = entryTime;
    }
    
    // Getters only (immutable)
    public String getTicketId() { return ticketId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public VehicleType getVehicleType() { return vehicleType; }
    public int getSpotNumber() { return spotNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
}

// Entities
class ParkingSpot {
    private final int spotNumber;
    private final SpotSize size;
    private SpotStatus status;
    private String vehicleNumber;
    
    public ParkingSpot(int spotNumber, SpotSize size) {
        this.spotNumber = spotNumber;
        this.size = size;
        this.status = SpotStatus.AVAILABLE;
    }
    
    public synchronized boolean park(String vehicleNumber) {
        if (status == SpotStatus.AVAILABLE) {
            this.vehicleNumber = vehicleNumber;
            this.status = SpotStatus.OCCUPIED;
            return true;
        }
        return false;
    }
    
    public synchronized void vacate() {
        this.vehicleNumber = null;
        this.status = SpotStatus.AVAILABLE;
    }
    
    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }
    
    public SpotSize getSize() { return size; }
    public int getSpotNumber() { return spotNumber; }
}

// Strategy Pattern for Pricing
interface PricingStrategy {
    double calculatePrice(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType);
}

class HourlyPricingStrategy implements PricingStrategy {
    private static final Map<VehicleType, Double> HOURLY_RATES = Map.of(
        VehicleType.BIKE, 10.0,
        VehicleType.CAR, 20.0,
        VehicleType.TRUCK, 50.0
    );
    
    @Override
    public double calculatePrice(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType) {
        long hours = ChronoUnit.HOURS.between(entryTime, exitTime);
        if (hours == 0) hours = 1; // Minimum 1 hour
        return hours * HOURLY_RATES.get(vehicleType);
    }
}

// Main Parking Lot (Singleton)
class ParkingLot {
    private static ParkingLot instance;
    private final List<ParkingSpot> spots;
    private final Map<String, Ticket> activeTickets;
    private final PricingStrategy pricingStrategy;
    private int ticketCounter;
    
    private ParkingLot(int smallSpots, int mediumSpots, int largeSpots) {
        this.spots = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.pricingStrategy = new HourlyPricingStrategy();
        this.ticketCounter = 1;
        
        // Initialize spots
        initializeSpots(smallSpots, mediumSpots, largeSpots);
    }
    
    public static synchronized ParkingLot getInstance(int small, int medium, int large) {
        if (instance == null) {
            instance = new ParkingLot(small, medium, large);
        }
        return instance;
    }
    
    private void initializeSpots(int small, int medium, int large) {
        int spotNumber = 1;
        for (int i = 0; i < small; i++) {
            spots.add(new ParkingSpot(spotNumber++, SpotSize.SMALL));
        }
        for (int i = 0; i < medium; i++) {
            spots.add(new ParkingSpot(spotNumber++, SpotSize.MEDIUM));
        }
        for (int i = 0; i < large; i++) {
            spots.add(new ParkingSpot(spotNumber++, SpotSize.LARGE));
        }
    }
    
    public Ticket parkVehicle(String vehicleNumber, VehicleType vehicleType) {
        SpotSize requiredSize = getRequiredSpotSize(vehicleType);
        
        // Find available spot
        ParkingSpot spot = spots.stream()
            .filter(s -> s.getSize() == requiredSize && s.isAvailable())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No available spot for " + vehicleType));
        
        // Park vehicle
        if (spot.park(vehicleNumber)) {
            String ticketId = "T" + ticketCounter++;
            Ticket ticket = new Ticket(ticketId, vehicleNumber, vehicleType, 
                                      spot.getSpotNumber(), LocalDateTime.now());
            activeTickets.put(ticketId, ticket);
            return ticket;
        }
        
        throw new RuntimeException("Failed to park vehicle");
    }
    
    public double unparkVehicle(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new RuntimeException("Invalid ticket");
        }
        
        // Find and vacate spot
        ParkingSpot spot = spots.stream()
            .filter(s -> s.getSpotNumber() == ticket.getSpotNumber())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Spot not found"));
        
        spot.vacate();
        
        // Calculate price
        return pricingStrategy.calculatePrice(
            ticket.getEntryTime(), 
            LocalDateTime.now(), 
            ticket.getVehicleType()
        );
    }
    
    public int getAvailableSpots(SpotSize size) {
        return (int) spots.stream()
            .filter(s -> s.getSize() == size && s.isAvailable())
            .count();
    }
    
    private SpotSize getRequiredSpotSize(VehicleType vehicleType) {
        return switch (vehicleType) {
            case BIKE -> SpotSize.SMALL;
            case CAR -> SpotSize.MEDIUM;
            case TRUCK -> SpotSize.LARGE;
        };
    }
}

// Demo
public class ParkingLotDemo {
    public static void main(String[] args) {
        // Initialize parking lot
        ParkingLot parkingLot = ParkingLot.getInstance(10, 10, 5);
        
        // Park vehicles
        Ticket ticket1 = parkingLot.parkVehicle("KA-01-1234", VehicleType.CAR);
        System.out.println("Parked car, ticket: " + ticket1.getTicketId());
        
        Ticket ticket2 = parkingLot.parkVehicle("KA-02-5678", VehicleType.BIKE);
        System.out.println("Parked bike, ticket: " + ticket2.getTicketId());
        
        // Check availability
        System.out.println("Available medium spots: " + 
            parkingLot.getAvailableSpots(SpotSize.MEDIUM));
        
        // Unpark and calculate fee
        double fee = parkingLot.unparkVehicle(ticket1.getTicketId());
        System.out.println("Parking fee: ₹" + fee);
    }
}
```

### Key Points to Mention
- **Thread Safety**: `synchronized` on spot operations, `ConcurrentHashMap` for tickets
- **Extensibility**: Easy to add new pricing strategies, vehicle types
- **SOLID Principles**: Single responsibility, Open-closed (strategy pattern)
- **Error Handling**: Meaningful exceptions with context

---

## 2. LRU Cache

### Problem Statement
Implement an LRU (Least Recently Used) cache with:
- `get(key)`: Get value if exists
- `put(key, value)`: Add/update key-value
- Fixed capacity, evict least recently used when full
- O(1) time complexity for both operations

### Interview Checklist

**Requirements:**
- [ ] Capacity limit
- [ ] Thread-safe or single-threaded?
- [ ] Eviction policy (strict LRU)
- [ ] Get operation updates access time?

**Design Decisions:**
- [ ] HashMap + Doubly Linked List
- [ ] LinkedHashMap (easier but show custom too)
- [ ] Thread safety with locks

**Implementation Focus:**
- [ ] O(1) operations proof
- [ ] Proper node management
- [ ] Edge cases (capacity 0, null keys)

### Solution

```java
class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final ReadWriteLock lock;
    
    // Doubly linked list node
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        
        // Dummy head and tail
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }
    
    public V get(K key) {
        lock.readLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) {
                return null;
            }
            
            // Move to front (most recently used)
            moveToFront(node);
            return node.value;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            
            if (node != null) {
                // Update existing node
                node.value = value;
                moveToFront(node);
            } else {
                // Add new node
                node = new Node<>(key, value);
                cache.put(key, node);
                addToFront(node);
                
                // Evict LRU if over capacity
                if (cache.size() > capacity) {
                    Node<K, V> lru = removeLast();
                    cache.remove(lru.key);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void moveToFront(Node<K, V> node) {
        removeNode(node);
        addToFront(node);
    }
    
    private void addToFront(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private Node<K, V> removeLast() {
        Node<K, V> last = tail.prev;
        removeNode(last);
        return last;
    }
    
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}

// Test
public class LRUCacheDemo {
    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        
        System.out.println(cache.get(1)); // "One" (1 is now most recent)
        
        cache.put(4, "Four"); // Evicts key 2 (least recently used)
        
        System.out.println(cache.get(2)); // null
        System.out.println(cache.get(1)); // "One"
        System.out.println(cache.get(3)); // "Three"
        System.out.println(cache.get(4)); // "Four"
    }
}
```

### Alternative: Using LinkedHashMap

```java
class LRUCacheSimple<K, V> {
    private final int capacity;
    private final Map<K, V> cache;
    
    public LRUCacheSimple(int capacity) {
        this.capacity = capacity;
        this.cache = Collections.synchronizedMap(
            new LinkedHashMap<K, V>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > LRUCacheSimple.this.capacity;
                }
            }
        );
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public void put(K key, V value) {
        cache.put(key, value);
    }
}
```

### Key Points to Mention
- **Time Complexity**: O(1) for both get and put
- **Space Complexity**: O(capacity)
- **Thread Safety**: ReadWriteLock for concurrent access
- **Trade-offs**: Custom impl vs LinkedHashMap

---

## 3. Tic-Tac-Toe Game

### Problem Statement
Design a Tic-Tac-Toe game that supports:
- N x N board (typically 3x3)
- 2 players
- Win detection
- Draw detection
- Move validation

### Interview Checklist

**Requirements:**
- [ ] Board size (3x3 or configurable N x N)
- [ ] Number of players (2 or more)
- [ ] Win condition (row, column, diagonal)
- [ ] AI opponent needed?

**Design Decisions:**
- [ ] Board representation (2D array)
- [ ] Player enum vs class
- [ ] Win detection algorithm
- [ ] Move validation strategy

**Implementation Focus:**
- [ ] Clean separation (Board, Game, Player)
- [ ] Efficient win detection
- [ ] Immutability where possible
- [ ] Clear game state management

### Solution

```java
enum Player {
    X('X'), O('O');
    
    private final char symbol;
    
    Player(char symbol) {
        this.symbol = symbol;
    }
    
    public char getSymbol() {
        return symbol;
    }
}

class Board {
    private final int size;
    private final char[][] grid;
    private int movesCount;
    
    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.movesCount = 0;
        
        // Initialize with empty
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '-';
            }
        }
    }
    
    public boolean makeMove(int row, int col, Player player) {
        if (!isValidMove(row, col)) {
            return false;
        }
        
        grid[row][col] = player.getSymbol();
        movesCount++;
        return true;
    }
    
    private boolean isValidMove(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return false;
        }
        return grid[row][col] == '-';
    }
    
    public boolean checkWin(Player player) {
        char symbol = player.getSymbol();
        
        // Check rows and columns
        for (int i = 0; i < size; i++) {
            if (checkRow(i, symbol) || checkColumn(i, symbol)) {
                return true;
            }
        }
        
        // Check diagonals
        return checkDiagonal(symbol) || checkAntiDiagonal(symbol);
    }
    
    private boolean checkRow(int row, char symbol) {
        for (int col = 0; col < size; col++) {
            if (grid[row][col] != symbol) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkColumn(int col, char symbol) {
        for (int row = 0; row < size; row++) {
            if (grid[row][col] != symbol) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkDiagonal(char symbol) {
        for (int i = 0; i < size; i++) {
            if (grid[i][i] != symbol) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkAntiDiagonal(char symbol) {
        for (int i = 0; i < size; i++) {
            if (grid[i][size - 1 - i] != symbol) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isFull() {
        return movesCount == size * size;
    }
    
    public void display() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

class TicTacToeGame {
    private final Board board;
    private Player currentPlayer;
    private boolean gameOver;
    
    public TicTacToeGame(int size) {
        this.board = new Board(size);
        this.currentPlayer = Player.X;
        this.gameOver = false;
    }
    
    public boolean playMove(int row, int col) {
        if (gameOver) {
            System.out.println("Game is over!");
            return false;
        }
        
        if (!board.makeMove(row, col, currentPlayer)) {
            System.out.println("Invalid move!");
            return false;
        }
        
        board.display();
        
        if (board.checkWin(currentPlayer)) {
            System.out.println("Player " + currentPlayer + " wins!");
            gameOver = true;
            return true;
        }
        
        if (board.isFull()) {
            System.out.println("Game is a draw!");
            gameOver = true;
            return true;
        }
        
        // Switch player
        currentPlayer = (currentPlayer == Player.X) ? Player.O : Player.X;
        return true;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
}

// Demo
public class TicTacToeDemo {
    public static void main(String[] args) {
        TicTacToeGame game = new TicTacToeGame(3);
        
        // Player X moves
        game.playMove(0, 0); // X
        game.playMove(1, 1); // O
        game.playMove(0, 1); // X
        game.playMove(1, 2); // O
        game.playMove(0, 2); // X - Wins!
    }
}
```

### Optimized Win Detection

```java
class OptimizedBoard {
    private final int size;
    private final char[][] grid;
    private final int[] rowCounts;
    private final int[] colCounts;
    private int diagonalCount;
    private int antiDiagonalCount;
    
    public OptimizedBoard(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.rowCounts = new int[size];
        this.colCounts = new int[size];
        this.diagonalCount = 0;
        this.antiDiagonalCount = 0;
    }
    
    public boolean makeMove(int row, int col, Player player) {
        if (!isValidMove(row, col)) return false;
        
        grid[row][col] = player.getSymbol();
        int value = (player == Player.X) ? 1 : -1;
        
        // Update counts
        rowCounts[row] += value;
        colCounts[col] += value;
        if (row == col) diagonalCount += value;
        if (row + col == size - 1) antiDiagonalCount += value;
        
        // Check for win (instant O(1) check)
        return Math.abs(rowCounts[row]) == size ||
               Math.abs(colCounts[col]) == size ||
               Math.abs(diagonalCount) == size ||
               Math.abs(antiDiagonalCount) == size;
    }
}
```

### Key Points to Mention
- **Win Detection**: O(n) naive vs O(1) optimized with counters
- **Scalability**: Works for any N x N board
- **Validation**: Comprehensive move validation
- **State Management**: Clear game state tracking

---

## 4. Meeting Scheduler

### Problem Statement
Design a meeting room booking system:
- Book meeting rooms for specific time slots
- Check room availability
- Avoid double booking
- Support multiple rooms

### Interview Checklist

**Requirements:**
- [ ] Single room or multiple rooms?
- [ ] Meeting duration granularity (minutes, hours)
- [ ] Recurring meetings support?
- [ ] Cancellation policy?
- [ ] Priority booking?

**Design Decisions:**
- [ ] TreeMap for time-based queries
- [ ] Interval overlap detection
- [ ] Builder pattern for meetings
- [ ] Observer pattern for notifications

**Implementation Focus:**
- [ ] Efficient availability check
- [ ] No double booking guarantee
- [ ] Clean interval handling
- [ ] Thread-safe operations

### Solution

```java
class TimeSlot implements Comparable<TimeSlot> {
    private final LocalDateTime start;
    private final LocalDateTime end;
    
    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start must be before end");
        }
        this.start = start;
        this.end = end;
    }
    
    public boolean overlaps(TimeSlot other) {
        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }
    
    @Override
    public int compareTo(TimeSlot other) {
        return this.start.compareTo(other.start);
    }
    
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    
    @Override
    public String toString() {
        return start + " to " + end;
    }
}

class Meeting {
    private final String id;
    private final String title;
    private final TimeSlot timeSlot;
    private final String organizer;
    private final List<String> participants;
    
    private Meeting(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.timeSlot = builder.timeSlot;
        this.organizer = builder.organizer;
        this.participants = new ArrayList<>(builder.participants);
    }
    
    public static class Builder {
        private String id;
        private String title;
        private TimeSlot timeSlot;
        private String organizer;
        private List<String> participants = new ArrayList<>();
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder timeSlot(TimeSlot timeSlot) {
            this.timeSlot = timeSlot;
            return this;
        }
        
        public Builder organizer(String organizer) {
            this.organizer = organizer;
            return this;
        }
        
        public Builder addParticipant(String participant) {
            this.participants.add(participant);
            return this;
        }
        
        public Meeting build() {
            if (id == null || timeSlot == null) {
                throw new IllegalStateException("ID and TimeSlot are required");
            }
            return new Meeting(this);
        }
    }
    
    public String getId() { return id; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public String getTitle() { return title; }
}

class MeetingRoom {
    private final String roomId;
    private final String name;
    private final int capacity;
    private final TreeMap<TimeSlot, Meeting> bookings; // Sorted by start time
    private final ReadWriteLock lock;
    
    public MeetingRoom(String roomId, String name, int capacity) {
        this.roomId = roomId;
        this.name = name;
        this.capacity = capacity;
        this.bookings = new TreeMap<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    public boolean isAvailable(TimeSlot timeSlot) {
        lock.readLock().lock();
        try {
            for (TimeSlot bookedSlot : bookings.keySet()) {
                if (timeSlot.overlaps(bookedSlot)) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public boolean book(Meeting meeting) {
        lock.writeLock().lock();
        try {
            TimeSlot timeSlot = meeting.getTimeSlot();
            if (!isAvailable(timeSlot)) {
                return false;
            }
            bookings.put(timeSlot, meeting);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public boolean cancelBooking(String meetingId) {
        lock.writeLock().lock();
        try {
            return bookings.entrySet()
                .removeIf(entry -> entry.getValue().getId().equals(meetingId));
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public List<Meeting> getBookings(LocalDate date) {
        lock.readLock().lock();
        try {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            return bookings.entrySet().stream()
                .filter(entry -> {
                    TimeSlot slot = entry.getKey();
                    return slot.getStart().isBefore(dayEnd) && 
                           slot.getEnd().isAfter(dayStart);
                })
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getRoomId() { return roomId; }
    public String getName() { return name; }
}

class MeetingScheduler {
    private final Map<String, MeetingRoom> rooms;
    private int meetingIdCounter;
    
    public MeetingScheduler() {
        this.rooms = new ConcurrentHashMap<>();
        this.meetingIdCounter = 1;
    }
    
    public void addRoom(MeetingRoom room) {
        rooms.put(room.getRoomId(), room);
    }
    
    public Meeting scheduleMeeting(String roomId, String title, TimeSlot timeSlot, 
                                   String organizer, List<String> participants) {
        MeetingRoom room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
        
        Meeting meeting = new Meeting.Builder()
            .id("M" + meetingIdCounter++)
            .title(title)
            .timeSlot(timeSlot)
            .organizer(organizer)
            .build();
        
        participants.forEach(p -> 
            new Meeting.Builder().addParticipant(p));
        
        if (room.book(meeting)) {
            System.out.println("Meeting scheduled: " + meeting.getTitle() + 
                             " in room " + room.getName());
            return meeting;
        } else {
            throw new RuntimeException("Room not available for the requested time");
        }
    }
    
    public List<MeetingRoom> findAvailableRooms(TimeSlot timeSlot) {
        return rooms.values().stream()
            .filter(room -> room.isAvailable(timeSlot))
            .collect(Collectors.toList());
    }
    
    public boolean cancelMeeting(String roomId, String meetingId) {
        MeetingRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }
        return room.cancelBooking(meetingId);
    }
}

// Demo
public class MeetingSchedulerDemo {
    public static void main(String[] args) {
        MeetingScheduler scheduler = new MeetingScheduler();
        
        // Add rooms
        scheduler.addRoom(new MeetingRoom("R1", "Conference Room A", 10));
        scheduler.addRoom(new MeetingRoom("R2", "Conference Room B", 5));
        
        // Schedule meetings
        LocalDateTime now = LocalDateTime.now();
        TimeSlot slot1 = new TimeSlot(now, now.plusHours(1));
        
        Meeting meeting = scheduler.scheduleMeeting(
            "R1", 
            "Sprint Planning", 
            slot1, 
            "john@company.com",
            List.of("alice@company.com", "bob@company.com")
        );
        
        // Try to double book (should fail)
        TimeSlot overlapping = new TimeSlot(now.plusMinutes(30), now.plusHours(2));
        try {
            scheduler.scheduleMeeting("R1", "Another Meeting", overlapping, 
                                     "alice@company.com", List.of());
        } catch (RuntimeException e) {
            System.out.println("Expected: " + e.getMessage());
        }
        
        // Find available rooms
        List<MeetingRoom> available = scheduler.findAvailableRooms(overlapping);
        System.out.println("Available rooms: " + 
            available.stream().map(MeetingRoom::getName).collect(Collectors.joining(", ")));
    }
}
```

### Key Points to Mention
- **Interval Overlap**: Efficient detection using start/end comparisons
- **TreeMap Usage**: Sorted storage for efficient range queries
- **Thread Safety**: ReadWriteLock for concurrent access
- **Builder Pattern**: Clean meeting creation with optional fields

---

## 5. Snake and Ladder Game

### Problem Statement
Simulate a Snake and Ladder board game:
- N x N board (typically 10x10, numbered 1-100)
- Multiple players
- Dice roll (1-6)
- Snakes send you down, ladders send you up
- First to reach 100 wins

### Interview Checklist

**Requirements:**
- [ ] Board size (10x10 standard)
- [ ] Number of players
- [ ] Exact 100 to win or any roll past 100?
- [ ] Multiple dice?
- [ ] Snake/ladder positions configurable?

**Design Decisions:**
- [ ] Board as HashMap (position → destination)
- [ ] Player queue for turn management
- [ ] Jump interface for polymorphism
- [ ] Game state enum

**Implementation Focus:**
- [ ] Clean turn logic
- [ ] Proper win condition
- [ ] Extensible design (can add power-ups)
- [ ] Simulation capability

### Solution

```java
interface Jump {
    int getStart();
    int getEnd();
}

class Snake implements Jump {
    private final int head;
    private final int tail;
    
    public Snake(int head, int tail) {
        if (head <= tail) {
            throw new IllegalArgumentException("Snake head must be > tail");
        }
        this.head = head;
        this.tail = tail;
    }
    
    @Override
    public int getStart() { return head; }
    
    @Override
    public int getEnd() { return tail; }
}

class Ladder implements Jump {
    private final int bottom;
    private final int top;
    
    public Ladder(int bottom, int top) {
        if (bottom >= top) {
            throw new IllegalArgumentException("Ladder bottom must be < top");
        }
        this.bottom = bottom;
        this.top = top;
    }
    
    @Override
    public int getStart() { return bottom; }
    
    @Override
    public int getEnd() { return top; }
}

class Player {
    private final String name;
    private int position;
    
    public Player(String name) {
        this.name = name;
        this.position = 0;
    }
    
    public void move(int steps) {
        this.position += steps;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public String getName() { return name; }
    public int getPosition() { return position; }
}

class Board {
    private final int size;
    private final Map<Integer, Jump> jumps;
    
    public Board(int size) {
        this.size = size;
        this.jumps = new HashMap<>();
    }
    
    public void addJump(Jump jump) {
        if (jump.getStart() < 1 || jump.getStart() > size ||
            jump.getEnd() < 1 || jump.getEnd() > size) {
            throw new IllegalArgumentException("Invalid jump positions");
        }
        jumps.put(jump.getStart(), jump);
    }
    
    public int getNewPosition(int position) {
        if (position > size) {
            return position; // Exceeded board
        }
        
        // Check for jump (snake or ladder)
        Jump jump = jumps.get(position);
        if (jump != null) {
            System.out.println("  " + 
                (jump instanceof Snake ? "Snake!" : "Ladder!") + 
                " Move from " + position + " to " + jump.getEnd());
            return jump.getEnd();
        }
        
        return position;
    }
    
    public int getSize() { return size; }
}

class Dice {
    private final int faces;
    private final Random random;
    
    public Dice(int faces) {
        this.faces = faces;
        this.random = new Random();
    }
    
    public int roll() {
        return random.nextInt(faces) + 1;
    }
}

class SnakeAndLadderGame {
    private final Board board;
    private final Dice dice;
    private final Queue<Player> players;
    private Player winner;
    
    public SnakeAndLadderGame(int boardSize, int diceFaces) {
        this.board = new Board(boardSize);
        this.dice = new Dice(diceFaces);
        this.players = new LinkedList<>();
    }
    
    public void addPlayer(Player player) {
        players.offer(player);
    }
    
    public void addSnake(int head, int tail) {
        board.addJump(new Snake(head, tail));
    }
    
    public void addLadder(int bottom, int top) {
        board.addJump(new Ladder(bottom, top));
    }
    
    public void play() {
        while (winner == null) {
            Player currentPlayer = players.poll();
            takeTurn(currentPlayer);
            
            if (currentPlayer.getPosition() == board.getSize()) {
                winner = currentPlayer;
                System.out.println("\n🎉 " + winner.getName() + " WINS! 🎉");
            } else {
                players.offer(currentPlayer); // Back to queue
            }
        }
    }
    
    private void takeTurn(Player player) {
        int roll = dice.roll();
        System.out.println(player.getName() + " rolled " + roll);
        
        int oldPosition = player.getPosition();
        int newPosition = oldPosition + roll;
        
        // Check if exceeds board
        if (newPosition > board.getSize()) {
            System.out.println("  Rolled too high! Stay at " + oldPosition);
            return;
        }
        
        player.setPosition(newPosition);
        System.out.println("  Moved from " + oldPosition + " to " + newPosition);
        
        // Check for snake or ladder
        int finalPosition = board.getNewPosition(newPosition);
        if (finalPosition != newPosition) {
            player.setPosition(finalPosition);
        }
        
        System.out.println("  Final position: " + player.getPosition());
        System.out.println();
    }
    
    public Player getWinner() {
        return winner;
    }
}

// Demo
public class SnakeAndLadderDemo {
    public static void main(String[] args) {
        SnakeAndLadderGame game = new SnakeAndLadderGame(100, 6);
        
        // Add players
        game.addPlayer(new Player("Alice"));
        game.addPlayer(new Player("Bob"));
        game.addPlayer(new Player("Charlie"));
        
        // Add snakes
        game.addSnake(99, 54);
        game.addSnake(70, 55);
        game.addSnake(52, 42);
        game.addSnake(25, 2);
        
        // Add ladders
        game.addLadder(6, 25);
        game.addLadder(11, 40);
        game.addLadder(60, 85);
        game.addLadder(46, 90);
        
        // Play game
        game.play();
    }
}
```

### Key Points to Mention
- **Polymorphism**: Jump interface for snakes and ladders
- **Queue Usage**: Fair turn management
- **Extensibility**: Easy to add new jump types (e.g., teleporters)
- **Game Rules**: Proper handling of edge cases (rolling past 100)

---

## Interview Success Tips

### Time Management
- **0-5 min**: Clarify requirements
- **5-15 min**: Design and discuss
- **15-60 min**: Implementation
- **60-70 min**: Testing and edge cases
- **70-75 min**: Discuss improvements

### Code Quality Checklist
- [ ] Meaningful variable/class names
- [ ] Proper encapsulation (private fields)
- [ ] Input validation
- [ ] Error handling
- [ ] Comments for complex logic
- [ ] No magic numbers (use constants)

### Common Pitfalls to Avoid
- ❌ Starting to code immediately without design
- ❌  Over-engineering (don't implement features not asked)
- ❌ Ignoring edge cases
- ❌ Poor naming conventions
- ❌ Not testing the code
- ❌ Messy code structure

### What Interviewers Look For
1. **Problem Understanding**: Do you ask the right questions?
2. **Design Skills**: Can you break down the problem?
3. **Code Quality**: Is your code clean and maintainable?
4. **Testing**: Do you think about edge cases?
5. **Communication**: Can you explain your approach?

---

**Total Problems**: 12 real-world machine coding challenges  
**Difficulty**: Medium to Hard (SDE2/SDE3 level)  
**Focus**: Production-ready, extensible, maintainable code

**Note**: Practice these problems multiple times. Start with designing on paper before coding!
