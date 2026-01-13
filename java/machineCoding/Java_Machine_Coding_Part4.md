# Java Machine Coding Round - Part 4 (Advanced Design Patterns for Interviews)

> **Target**: SDE2/SDE3 positions  
> **Duration**: Typically 60-90 minutes per question  
> **Focus**: Advanced design patterns and real-world system design

---

## Table of Contents

13. [Observer Pattern - Stock Trading System](#13-observer-pattern---stock-trading-system)
14. [Chain of Responsibility - Logging Framework](#14-chain-of-responsibility---logging-framework)
15. [Command Pattern - Text Editor with Undo/Redo](#15-command-pattern---text-editor-with-undoredo)
16. [Decorator Pattern - Coffee Shop](#16-decorator-pattern---coffee-shop)

---

## 13. Observer Pattern - Stock Trading System

### Problem Statement
Design a stock trading system that:
- Multiple traders can subscribe to stock price updates
- Notify all subscribers when price changes
- Support different notification strategies (email, SMS, push)
- Remove inactive subscribers
- Handle high-frequency updates

### Interview Checklist

**Requirements:**
- [ ] Number of stocks and traders
- [ ] Notification frequency
- [ ] Synchronous or asynchronous notifications?
- [ ] Notification delivery guarantees
- [ ] Historical price tracking needed?

**Design Decisions:**
- [ ] Observer pattern for publish-subscribe
- [ ] Strategy pattern for notification methods
- [ ] Weak references to prevent memory leaks
- [ ] Thread pool for async notifications

**Implementation Focus:**
- [ ] Thread-safe notification
- [ ] Efficient observer management
- [ ] Avoiding memory leaks
- [ ] Handling slow observers

### Solution

```java
// Subject interface
interface StockSubject {
    void attach(StockObserver observer);
    void detach(StockObserver observer);
    void notifyObservers();
}

// Observer interface
interface StockObserver {
    void update(Stock stock, double oldPrice, double newPrice);
    String getObserverId();
}

// Stock (Concrete Subject)
class Stock implements StockSubject {
    private final String symbol;
    private final String name;
    private double price;
    private final List<StockObserver> observers;
    private final ReadWriteLock lock;
    private final ExecutorService notificationExecutor;
    
    public Stock(String symbol, String name, double initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.price = initialPrice;
        this.observers = new CopyOnWriteArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.notificationExecutor = Executors.newFixedThreadPool(5);
    }
    
    @Override
    public void attach(StockObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Observer " + observer.getObserverId() + 
                             " subscribed to " + symbol);
        }
    }
    
    @Override
    public void detach(StockObserver observer) {
        if (observers.remove(observer)) {
            System.out.println("Observer " + observer.getObserverId() + 
                             " unsubscribed from " + symbol);
        }
    }
    
    @Override
    public void notifyObservers() {
        // Use executor for async notifications to avoid blocking
        List<StockObserver> currentObservers = new ArrayList<>(observers);
        for (StockObserver observer : currentObservers) {
            notificationExecutor.submit(() -> {
                try {
                    observer.update(this, price, price);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            });
        }
    }
    
    public void setPrice(double newPrice) {
        lock.writeLock().lock();
        try {
            double oldPrice = this.price;
            this.price = newPrice;
            System.out.println(symbol + " price changed: " + oldPrice + " → " + newPrice);
            
            // Notify all observers
            notifyPriceChange(oldPrice, newPrice);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void notifyPriceChange(double oldPrice, double newPrice) {
        List<StockObserver> currentObservers = new ArrayList<>(observers);
        for (StockObserver observer : currentObservers) {
            notificationExecutor.submit(() -> {
                try {
                    observer.update(this, oldPrice, newPrice);
                } catch (Exception e) {
                    System.err.println("Error notifying observer: " + e.getMessage());
                    // Optionally remove failed observers
                    detach(observer);
                }
            });
        }
    }
    
    public double getPrice() {
        lock.readLock().lock();
        try {
            return price;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    
    public void shutdown() {
        notificationExecutor.shutdown();
    }
}

// Strategy Pattern for Notification Methods
interface NotificationStrategy {
    void notify(String message);
}

class EmailNotification implements NotificationStrategy {
    private final String email;
    
    public EmailNotification(String email) {
        this.email = email;
    }
    
    @Override
    public void notify(String message) {
        System.out.println("📧 Email to " + email + ": " + message);
    }
}

class SMSNotification implements NotificationStrategy {
    private final String phoneNumber;
    
    public SMSNotification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public void notify(String message) {
        System.out.println("📱 SMS to " + phoneNumber + ": " + message);
    }
}

class PushNotification implements NotificationStrategy {
    private final String deviceId;
    
    public PushNotification(String deviceId) {
        this.deviceId = deviceId;
    }
    
    @Override
    public void notify(String message) {
        System.out.println("🔔 Push to device " + deviceId + ": " + message);
    }
}

// Concrete Observer
class Trader implements StockObserver {
    private final String traderId;
    private final String name;
    private final double priceThreshold;
    private final List<NotificationStrategy> notificationStrategies;
    
    public Trader(String traderId, String name, double priceThreshold) {
        this.traderId = traderId;
        this.name = name;
        this.priceThreshold = priceThreshold;
        this.notificationStrategies = new ArrayList<>();
    }
    
    public void addNotificationStrategy(NotificationStrategy strategy) {
        notificationStrategies.add(strategy);
    }
    
    @Override
    public void update(Stock stock, double oldPrice, double newPrice) {
        double changePercent = ((newPrice - oldPrice) / oldPrice) * 100;
        
        // Only notify if significant change
        if (Math.abs(changePercent) >= priceThreshold) {
            String message = String.format(
                "[%s] %s: %.2f%% change (%.2f → %.2f)",
                stock.getSymbol(),
                name,
                changePercent,
                oldPrice,
                newPrice
            );
            
            // Send notifications via all strategies
            for (NotificationStrategy strategy : notificationStrategies) {
                strategy.notify(message);
            }
        }
    }
    
    @Override
    public String getObserverId() {
        return traderId;
    }
    
    public String getName() { return name; }
}

// Portfolio Manager (Advanced Observer)
class PortfolioManager implements StockObserver {
    private final String managerId;
    private final Map<String, Integer> holdings; // symbol -> quantity
    private volatile double portfolioValue;
    
    public PortfolioManager(String managerId) {
        this.managerId = managerId;
        this.holdings = new ConcurrentHashMap<>();
        this.portfolioValue = 0.0;
    }
    
    public void addHolding(String symbol, int quantity) {
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + quantity);
    }
    
    @Override
    public void update(Stock stock, double oldPrice, double newPrice) {
        Integer quantity = holdings.get(stock.getSymbol());
        if (quantity != null && quantity > 0) {
            double oldValue = oldPrice * quantity;
            double newValue = newPrice * quantity;
            double change = newValue - oldValue;
            
            portfolioValue += change;
            
            System.out.println(String.format(
                "📊 Portfolio [%s]: %s changed by $%.2f (Value: $%.2f)",
                managerId,
                stock.getSymbol(),
                change,
                portfolioValue
            ));
        }
    }
    
    @Override
    public String getObserverId() {
        return managerId;
    }
    
    public double getPortfolioValue() {
        return portfolioValue;
    }
}

// Demo
public class StockTradingDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create stocks
        Stock appleStock = new Stock("AAPL", "Apple Inc.", 150.0);
        Stock googleStock = new Stock("GOOGL", "Alphabet Inc.", 2800.0);
        
        // Create traders
        Trader trader1 = new Trader("T001", "Alice", 2.0); // 2% threshold
        trader1.addNotificationStrategy(new EmailNotification("alice@example.com"));
        trader1.addNotificationStrategy(new SMSNotification("+1-555-0001"));
        
        Trader trader2 = new Trader("T002", "Bob", 5.0); // 5% threshold
        trader2.addNotificationStrategy(new PushNotification("device123"));
        
        // Create portfolio manager
        PortfolioManager portfolioManager = new PortfolioManager("PM001");
        portfolioManager.addHolding("AAPL", 100);
        portfolioManager.addHolding("GOOGL", 50);
        
        // Subscribe to stocks
        appleStock.attach(trader1);
        appleStock.attach(trader2);
        appleStock.attach(portfolioManager);
        
        googleStock.attach(trader1);
        googleStock.attach(portfolioManager);
        
        // Simulate price changes
        System.out.println("\n=== Price Updates ===\n");
        
        appleStock.setPrice(153.0); // 2% increase
        Thread.sleep(1000);
        
        appleStock.setPrice(155.0); // Additional 1.3% increase
        Thread.sleep(1000);
        
        googleStock.setPrice(2950.0); // 5.4% increase
        Thread.sleep(1000);
        
        // Unsubscribe trader2 from Apple
        System.out.println("\n=== Unsubscribing ===\n");
        appleStock.detach(trader2);
        
        appleStock.setPrice(160.0); // 3.2% increase
        Thread.sleep(1000);
        
        // Print portfolio value
        System.out.println("\n=== Final Portfolio Value ===");
        System.out.println("Portfolio Value: $" + portfolioManager.getPortfolioValue());
        
        // Cleanup
        appleStock.shutdown();
        googleStock.shutdown();
    }
}
```

### Key Points to Mention
- **Loose Coupling**: Observers and subjects are decoupled
- **Async Notifications**: Using ExecutorService to avoid blocking
- **Thread Safety**: CopyOnWriteArrayList for concurrent modifications
- **Memory Management**: Proper observer cleanup to prevent leaks
- **Strategy Pattern**: Different notification methods (email, SMS, push)

---

## 14. Chain of Responsibility - Logging Framework

### Problem Statement
Design a logging framework that:
- Multiple log levels (DEBUG, INFO, WARN, ERROR)
- Chain of handlers for different outputs (console, file, database)
- Filtering based on log level
- Support custom formatters
- Thread-safe logging

### Interview Checklist

**Requirements:**
- [ ] Log levels hierarchy
- [ ] Output destinations (console, file, remote)
- [ ] Log formatting requirements
- [ ] Performance considerations
- [ ] Asynchronous logging needed?

**Design Decisions:**
- [ ] Chain of Responsibility for handlers
- [ ] Strategy pattern for formatters
- [ ] Builder pattern for logger configuration
- [ ] Queue for async logging

**Implementation Focus:**
- [ ] Proper chain traversal
- [ ] Level filtering logic
- [ ] Thread-safe file writing
- [ ] Buffer management

### Solution

```java
enum LogLevel {
    DEBUG(1), INFO(2), WARN(3), ERROR(4), FATAL(5);
    
    private final int priority;
    
    LogLevel(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public boolean shouldLog(LogLevel messageLevel) {
        return messageLevel.priority >= this.priority;
    }
}

class LogRecord {
    private final LogLevel level;
    private final String message;
    private final LocalDateTime timestamp;
    private final String threadName;
    private final StackTraceElement caller;
    
    public LogRecord(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.threadName = Thread.currentThread().getName();
        
        // Get caller info
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        this.caller = stackTrace.length > 3 ? stackTrace[3] : null;
    }
    
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getThreadName() { return threadName; }
    public StackTraceElement getCaller() { return caller; }
}

// Strategy Pattern for Log Formatting
interface LogFormatter {
    String format(LogRecord record);
}

class SimpleFormatter implements LogFormatter {
    @Override
    public String format(LogRecord record) {
        return String.format("[%s] %s: %s",
            record.getLevel(),
            record.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            record.getMessage()
        );
    }
}

class DetailedFormatter implements LogFormatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(record.getLevel()).append("] ");
        sb.append(record.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        sb.append(" [").append(record.getThreadName()).append("] ");
        
        if (record.getCaller() != null) {
            sb.append(record.getCaller().getClassName())
              .append(".")
              .append(record.getCaller().getMethodName())
              .append(":")
              .append(record.getCaller().getLineNumber());
        }
        
        sb.append(" - ").append(record.getMessage());
        return sb.toString();
    }
}

class JsonFormatter implements LogFormatter {
    @Override
    public String format(LogRecord record) {
        return String.format(
            "{\"level\":\"%s\",\"timestamp\":\"%s\",\"thread\":\"%s\",\"message\":\"%s\"}",
            record.getLevel(),
            record.getTimestamp(),
            record.getThreadName(),
            record.getMessage().replace("\"", "\\\"")
        );
    }
}

// Chain of Responsibility Handler
abstract class LogHandler {
    protected LogLevel level;
    protected LogHandler next;
    protected LogFormatter formatter;
    
    public LogHandler(LogLevel level, LogFormatter formatter) {
        this.level = level;
        this.formatter = formatter;
    }
    
    public void setNext(LogHandler next) {
        this.next = next;
    }
    
    public void handle(LogRecord record) {
        if (level.shouldLog(record.getLevel())) {
            write(record);
        }
        
        // Pass to next handler in chain
        if (next != null) {
            next.handle(record);
        }
    }
    
    protected abstract void write(LogRecord record);
}

// Console Handler
class ConsoleHandler extends LogHandler {
    private final PrintStream output;
    
    public ConsoleHandler(LogLevel level, LogFormatter formatter) {
        super(level, formatter);
        this.output = System.out;
    }
    
    @Override
    protected void write(LogRecord record) {
        synchronized (output) {
            String formattedMessage = formatter.format(record);
            
            // Color coding based on level
            if (record.getLevel() == LogLevel.ERROR || record.getLevel() == LogLevel.FATAL) {
                System.err.println(formattedMessage);
            } else {
                output.println(formattedMessage);
            }
        }
    }
}

// File Handler
class FileHandler extends LogHandler {
    private final String filename;
    private final BufferedWriter writer;
    private final Lock lock;
    
    public FileHandler(LogLevel level, LogFormatter formatter, String filename) throws IOException {
        super(level, formatter);
        this.filename = filename;
        this.writer = new BufferedWriter(new FileWriter(filename, true)); // Append mode
        this.lock = new ReentrantLock();
    }
    
    @Override
    protected void write(LogRecord record) {
        lock.lock();
        try {
            String formattedMessage = formatter.format(record);
            writer.write(formattedMessage);
            writer.newLine();
            writer.flush(); // Ensure immediate write
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    
    public void close() throws IOException {
        lock.lock();
        try {
            writer.close();
        } finally {
            lock.unlock();
        }
    }
}

// Database Handler (Simulated)
class DatabaseHandler extends LogHandler {
    private final Queue<String> buffer;
    private final int batchSize;
    private final Lock lock;
    
    public DatabaseHandler(LogLevel level, LogFormatter formatter, int batchSize) {
        super(level, formatter);
        this.buffer = new LinkedList<>();
        this.batchSize = batchSize;
        this.lock = new ReentrantLock();
    }
    
    @Override
    protected void write(LogRecord record) {
        lock.lock();
        try {
            String formattedMessage = formatter.format(record);
            buffer.offer(formattedMessage);
            
            // Batch insert when buffer is full
            if (buffer.size() >= batchSize) {
                flush();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void flush() {
        lock.lock();
        try {
            if (!buffer.isEmpty()) {
                System.out.println("💾 Writing " + buffer.size() + " logs to database");
                // Simulate database write
                buffer.clear();
            }
        } finally {
            lock.unlock();
        }
    }
}

// Logger (Using Builder Pattern)
class Logger {
    private final String name;
    private final LogHandler handlerChain;
    private final BlockingQueue<LogRecord> logQueue;
    private final ExecutorService logExecutor;
    private volatile boolean running;
    
    private Logger(Builder builder) {
        this.name = builder.name;
        this.handlerChain = builder.firstHandler;
        this.logQueue = new LinkedBlockingQueue<>(1000);
        this.logExecutor = Executors.newSingleThreadExecutor();
        this.running = true;
        
        // Start async log processor
        if (builder.async) {
            startAsyncProcessing();
        }
    }
    
    private void startAsyncProcessing() {
        logExecutor.submit(() -> {
            while (running || !logQueue.isEmpty()) {
                try {
                    LogRecord record = logQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (record != null) {
                        handlerChain.handle(record);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
    
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    public void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }
    
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }
    
    private void log(LogLevel level, String message) {
        LogRecord record = new LogRecord(level, message);
        
        try {
            logQueue.offer(record, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Fallback to synchronous logging
            handlerChain.handle(record);
            Thread.currentThread().interrupt();
        }
    }
    
    public void shutdown() {
        running = false;
        logExecutor.shutdown();
        try {
            logExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logExecutor.shutdownNow();
        }
    }
    
    // Builder Pattern
    public static class Builder {
        private String name;
        private LogHandler firstHandler;
        private LogHandler lastHandler;
        private boolean async = true;
        
        public Builder(String name) {
            this.name = name;
        }
        
        public Builder addHandler(LogHandler handler) {
            if (firstHandler == null) {
                firstHandler = handler;
                lastHandler = handler;
            } else {
                lastHandler.setNext(handler);
                lastHandler = handler;
            }
            return this;
        }
        
        public Builder setAsync(boolean async) {
            this.async = async;
            return this;
        }
        
        public Logger build() {
            if (firstHandler == null) {
                throw new IllegalStateException("At least one handler must be configured");
            }
            return new Logger(this);
        }
    }
}

// Demo
public class LoggingDemo {
    public static void main(String[] args) throws Exception {
        // Create formatters
        LogFormatter simpleFormatter = new SimpleFormatter();
        LogFormatter detailedFormatter = new DetailedFormatter();
        LogFormatter jsonFormatter = new JsonFormatter();
        
        // Create handlers
        ConsoleHandler consoleHandler = new ConsoleHandler(LogLevel.DEBUG, detailedFormatter);
        FileHandler fileHandler = new FileHandler(LogLevel.INFO, simpleFormatter, "application.log");
        DatabaseHandler dbHandler = new DatabaseHandler(LogLevel.ERROR, jsonFormatter, 5);
        
        // Build logger with chain of handlers
        Logger logger = new Logger.Builder("MyApp")
            .addHandler(consoleHandler)
            .addHandler(fileHandler)
            .addHandler(dbHandler)
            .setAsync(true)
            .build();
        
        // Log messages
        System.out.println("=== Logging Demo ===\n");
        
        logger.debug("This is a debug message");
        logger.info("Application started successfully");
        logger.warn("Memory usage is high");
        logger.error("Failed to connect to database");
        logger.fatal("Critical system failure");
        
        // Simulate some work
        Thread.sleep(1000);
        
        logger.info("Processing batch job");
        logger.error("Batch job failed - file not found");
        
        // Cleanup
        Thread.sleep(2000);
        dbHandler.flush();
        fileHandler.close();
        logger.shutdown();
        
        System.out.println("\n=== Logging Complete ===");
    }
}
```

### Key Points to Mention
- **Chain of Responsibility**: Handlers can be added/removed dynamically
- **Separation of Concerns**: Each handler has single responsibility
- **Async Logging**: Non-blocking log writes using queue
- **Buffering**: Database handler batches writes for efficiency
- **Thread Safety**: Proper synchronization for file and console writes
- **Extensibility**: Easy to add new handlers or formatters

---

## 15. Command Pattern - Text Editor with Undo/Redo

### Problem Statement
Design a text editor with:
- Basic editing operations (insert, delete, replace)
- Undo/Redo functionality
- Macro recording and playback
- Command history
- Limit undo stack size

### Interview Checklist

**Requirements:**
- [ ] Maximum undo/redo stack size
- [ ] Macro support needed?
- [ ] Clipboard operations?
- [ ] Multi-cursor support?
- [ ] Performance for large documents

**Design Decisions:**
- [ ] Command pattern for operations
- [ ] Two stacks for undo/redo
- [ ] Memento pattern for state saving
- [ ] Composite command for macros

**Implementation Focus:**
- [ ] Efficient undo/redo
- [ ] Command composition
- [ ] Memory management
- [ ] State restoration

### Solution

```java
// Command Interface
interface Command {
    void execute();
    void undo();
    String getDescription();
}

// Document (Receiver)
class TextDocument {
    private StringBuilder content;
    private int cursorPosition;
    
    public TextDocument() {
        this.content = new StringBuilder();
        this.cursorPosition = 0;
    }
    
    public void insert(int position, String text) {
        if (position < 0 || position > content.length()) {
            throw new IllegalArgumentException("Invalid position");
        }
        content.insert(position, text);
        cursorPosition = position + text.length();
    }
    
    public void delete(int start, int end) {
        if (start < 0 || end > content.length() || start > end) {
            throw new IllegalArgumentException("Invalid range");
        }
        content.delete(start, end);
        cursorPosition = start;
    }
    
    public void replace(int start, int end, String text) {
        delete(start, end);
        insert(start, text);
    }
    
    public String getText() {
        return content.toString();
    }
    
    public int getCursorPosition() {
        return cursorPosition;
    }
    
    public void setCursorPosition(int position) {
        if (position < 0 || position > content.length()) {
            throw new IllegalArgumentException("Invalid cursor position");
        }
        this.cursorPosition = position;
    }
    
    public int getLength() {
        return content.length();
    }
}

// Concrete Commands
class InsertCommand implements Command {
    private final TextDocument document;
    private final int position;
    private final String text;
    
    public InsertCommand(TextDocument document, int position, String text) {
        this.document = document;
        this.position = position;
        this.text = text;
    }
    
    @Override
    public void execute() {
        document.insert(position, text);
    }
    
    @Override
    public void undo() {
        document.delete(position, position + text.length());
    }
    
    @Override
    public String getDescription() {
        return "Insert '" + text + "' at position " + position;
    }
}

class DeleteCommand implements Command {
    private final TextDocument document;
    private final int start;
    private final int end;
    private String deletedText;
    
    public DeleteCommand(TextDocument document, int start, int end) {
        this.document = document;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public void execute() {
        // Save deleted text for undo
        deletedText = document.getText().substring(start, end);
        document.delete(start, end);
    }
    
    @Override
    public void undo() {
        document.insert(start, deletedText);
    }
    
    @Override
    public String getDescription() {
        return "Delete characters from " + start + " to " + end;
    }
}

class ReplaceCommand implements Command {
    private final TextDocument document;
    private final int start;
    private final int end;
    private final String newText;
    private String oldText;
    
    public ReplaceCommand(TextDocument document, int start, int end, String newText) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.newText = newText;
    }
    
    @Override
    public void execute() {
        oldText = document.getText().substring(start, end);
        document.replace(start, end, newText);
    }
    
    @Override
    public void undo() {
        document.replace(start, start + newText.length(), oldText);
    }
    
    @Override
    public String getDescription() {
        return "Replace '" + oldText + "' with '" + newText + "'";
    }
}

// Composite Command for Macros
class MacroCommand implements Command {
    private final List<Command> commands;
    private final String macroName;
    
    public MacroCommand(String macroName) {
        this.macroName = macroName;
        this.commands = new ArrayList<>();
    }
    
    public void addCommand(Command command) {
        commands.add(command);
    }
    
    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }
    
    @Override
    public void undo() {
        // Undo in reverse order
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }
    
    @Override
    public String getDescription() {
        return "Macro: " + macroName + " (" + commands.size() + " commands)";
    }
}

// Text Editor (Invoker)
class TextEditor {
    private final TextDocument document;
    private final Stack<Command> undoStack;
    private final Stack<Command> redoStack;
    private final int maxHistorySize;
    private boolean recordingMacro;
    private MacroCommand currentMacro;
    
    public TextEditor(int maxHistorySize) {
        this.document = new TextDocument();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.maxHistorySize = maxHistorySize;
        this.recordingMacro = false;
    }
    
    public void executeCommand(Command command) {
        command.execute();
        
        // Add to undo stack
        undoStack.push(command);
        
        // Limit stack size
        if (undoStack.size() > maxHistorySize) {
            undoStack.remove(0);
        }
        
        // Clear redo stack after new command
        redoStack.clear();
        
        // Add to macro if recording
        if (recordingMacro && currentMacro != null) {
            currentMacro.addCommand(command);
        }
        
        System.out.println("✓ " + command.getDescription());
    }
    
    public void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return;
        }
        
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        
        System.out.println("↶ Undo: " + command.getDescription());
    }
    
    public void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo");
            return;
        }
        
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        
        System.out.println("↷ Redo: " + command.getDescription());
    }
    
    public void startMacroRecording(String macroName) {
        currentMacro = new MacroCommand(macroName);
        recordingMacro = true;
        System.out.println("🔴 Recording macro: " + macroName);
    }
    
    public MacroCommand stopMacroRecording() {
        if (!recordingMacro) {
            throw new IllegalStateException("Not recording a macro");
        }
        recordingMacro = false;
        MacroCommand macro = currentMacro;
        currentMacro = null;
        System.out.println("⏹ Stopped recording macro");
        return macro;
    }
    
    public void showHistory() {
        System.out.println("\n--- Command History ---");
        System.out.println("Undo Stack (" + undoStack.size() + " commands):");
        for (int i = undoStack.size() - 1; i >= 0; i--) {
            System.out.println("  " + (undoStack.size() - i) + ". " + 
                             undoStack.get(i).getDescription());
        }
        
        if (!redoStack.isEmpty()) {
            System.out.println("Redo Stack (" + redoStack.size() + " commands):");
            for (int i = redoStack.size() - 1; i >= 0; i--) {
                System.out.println("  " + (redoStack.size() - i) + ". " + 
                                 redoStack.get(i).getDescription());
            }
        }
        System.out.println();
    }
    
    public String getText() {
        return document.getText();
    }
    
    public TextDocument getDocument() {
        return document;
    }
}

// Demo
public class TextEditorDemo {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor(10);
        
        System.out.println("=== Text Editor Demo ===\n");
        
        // Basic operations
        editor.executeCommand(new InsertCommand(editor.getDocument(), 0, "Hello"));
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        editor.executeCommand(new InsertCommand(editor.getDocument(), 5, " World"));
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        editor.executeCommand(new InsertCommand(editor.getDocument(), 11, "!"));
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        // Undo operations
        System.out.println("--- Undo Operations ---");
        editor.undo();
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        editor.undo();
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        // Redo operations
        System.out.println("--- Redo Operations ---");
        editor.redo();
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        // Macro recording
        System.out.println("--- Macro Recording ---");
        editor.startMacroRecording("AddGreeting");
        editor.executeCommand(new InsertCommand(editor.getDocument(), 0, "Hi! "));
        editor.executeCommand(new ReplaceCommand(editor.getDocument(), 4, 9, "Earth"));
        MacroCommand greetingMacro = editor.stopMacroRecording();
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        // Undo macro
        System.out.println("--- Undo Macro ---");
        editor.undo();
        System.out.println("Text: '" + editor.getText() + "'\n");
        
        // Show history
        editor.showHistory();
        
        // Final text
        System.out.println("Final Text: '" + editor.getText() + "'");
    }
}
```

### Key Points to Mention
- **Command Pattern**: Encapsulates operations as objects
- **Undo/Redo**: Two stacks for navigation
- **Macro Support**: Composite command for grouping operations
- **Memento**: Commands store state for undo
- **Decoupling**: Editor doesn't know command implementation details

---

## 16. Decorator Pattern - Coffee Shop

### Problem Statement
Design a coffee ordering system with:
- Base beverages (Espresso, Latte, Cappuccino)
- Add-ons/decorators (Milk, Sugar, Whipped Cream, Caramel)
- Calculate total cost dynamically
- Generate receipt with itemized list
- Support multiple decorators

### Interview Checklist

**Requirements:**
- [ ] Number of base beverages
- [ ] Available add-ons
- [ ] Pricing model
- [ ] Order modifications
- [ ] Size variations needed?

**Design Decisions:**
- [ ] Decorator pattern for add-ons
- [ ] Abstract component for beverages
- [ ] Builder pattern for complex orders (optional)
- [ ] Strategy for pricing

**Implementation Focus:**
- [ ] Recursive cost calculation
- [ ] Description building
- [ ] Proper decorator wrapping
- [ ] Extensibility

### Solution

```java
// Component Interface
interface Beverage {
    String getDescription();
    double cost();
    List<String> getIngredients();
}

// Concrete Components (Base Beverages)
class Espresso implements Beverage {
    @Override
    public String getDescription() {
        return "Espresso";
    }
    
    @Override
    public double cost() {
        return 2.50;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Espresso Shot");
        return ingredients;
    }
}

class Latte implements Beverage {
    @Override
    public String getDescription() {
        return "Latte";
    }
    
    @Override
    public double cost() {
        return 3.50;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Espresso Shot");
        ingredients.add("Steamed Milk");
        return ingredients;
    }
}

class Cappuccino implements Beverage {
    @Override
    public String getDescription() {
        return "Cappuccino";
    }
    
    @Override
    public double cost() {
        return 3.75;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Espresso Shot");
        ingredients.add("Steamed Milk");
        ingredients.add("Milk Foam");
        return ingredients;
    }
}

// Abstract Decorator
abstract class BeverageDecorator implements Beverage {
    protected Beverage beverage;
    
    public BeverageDecorator(Beverage beverage) {
        this.beverage = beverage;
    }
    
    @Override
    public String getDescription() {
        return beverage.getDescription();
    }
    
    @Override
    public List<String> getIngredients() {
        return beverage.getIngredients();
    }
}

// Concrete Decorators
class Milk extends BeverageDecorator {
    public Milk(Beverage beverage) {
        super(beverage);
    }
    
    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Milk";
    }
    
    @Override
    public double cost() {
        return beverage.cost() + 0.50;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(beverage.getIngredients());
        ingredients.add("Extra Milk");
        return ingredients;
    }
}

class Sugar extends BeverageDecorator {
    private final int packets;
    
    public Sugar(Beverage beverage, int packets) {
        super(beverage);
        this.packets = packets;
    }
    
    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Sugar (" + packets + " packets)";
    }
    
    @Override
    public double cost() {
        return beverage.cost() + (0.25 * packets);
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(beverage.getIngredients());
        ingredients.add("Sugar (" + packets + "x)");
        return ingredients;
    }
}

class WhippedCream extends BeverageDecorator {
    public WhippedCream(Beverage beverage) {
        super(beverage);
    }
    
    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Whipped Cream";
    }
    
    @Override
    public double cost() {
        return beverage.cost() + 0.75;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(beverage.getIngredients());
        ingredients.add("Whipped Cream");
        return ingredients;
    }
}

class Caramel extends BeverageDecorator {
    public Caramel(Beverage beverage) {
        super(beverage);
    }
    
    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Caramel Syrup";
    }
    
    @Override
    public double cost() {
        return beverage.cost() + 0.60;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(beverage.getIngredients());
        ingredients.add("Caramel Syrup");
        return ingredients;
    }
}

class Vanilla extends BeverageDecorator {
    public Vanilla(Beverage beverage) {
        super(beverage);
    }
    
    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Vanilla Syrup";
    }
    
    @Override
    public double cost() {
        return beverage.cost() + 0.60;
    }
    
    @Override
    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>(beverage.getIngredients());
        ingredients.add("Vanilla Syrup");
        return ingredients;
    }
}

// Size Decorator
enum Size { SMALL(0.0), MEDIUM(0.50), LARGE(1.00); 
    private final double price;
    Size(double price) { this.price = price; }
    public double getPrice() { return price; }
}

class SizeDecorator extends BeverageDecorator {
    private final Size size;
    
    public SizeDecorator(Beverage beverage, Size size) {
        super(beverage);
        this.size = size;
    }
    
    @Override
    public String getDescription() {
        return size + " " + beverage.getDescription();
    }
    
    @Override
    public double cost() {
        return beverage.cost() + size.getPrice();
    }
}

// Order Receipt
class CoffeeOrder {
    private final Beverage beverage;
    private final String customerName;
    private final LocalDateTime orderTime;
    
    public CoffeeOrder(Beverage beverage, String customerName) {
        this.beverage = beverage;
        this.customerName = customerName;
        this.orderTime = LocalDateTime.now();
    }
    
    public void printReceipt() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("☕ COFFEE SHOP RECEIPT");
        System.out.println("=".repeat(50));
        System.out.println("Customer: " + customerName);
        System.out.println("Time: " + orderTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("-".repeat(50));
        System.out.println("Order: " + beverage.getDescription());
        System.out.println("\nIngredients:");
        for (String ingredient : beverage.getIngredients()) {
            System.out.println("  • " + ingredient);
        }
        System.out.println("-".repeat(50));
        System.out.printf("Total: $%.2f\n", beverage.cost());
        System.out.println("=".repeat(50) + "\n");
    }
    
    public double getTotal() {
        return beverage.cost();
    }
}

// Demo
public class CoffeeShopDemo {
    public static void main(String[] args) {
        System.out.println("=== Coffee Shop Ordering System ===\n");
        
        // Order 1: Simple Espresso
        Beverage order1 = new Espresso();
        CoffeeOrder receipt1 = new CoffeeOrder(order1, "Alice");
        receipt1.printReceipt();
        
        // Order 2: Latte with extra milk and sugar
        Beverage order2 = new Latte();
        order2 = new Milk(order2);
        order2 = new Sugar(order2, 2);
        order2 = new SizeDecorator(order2, Size.LARGE);
        
        CoffeeOrder receipt2 = new CoffeeOrder(order2, "Bob");
        receipt2.printReceipt();
        
        // Order 3: Cappuccino with all the extras
        Beverage order3 = new Cappuccino();
        order3 = new WhippedCream(order3);
        order3 = new Caramel(order3);
        order3 = new Vanilla(order3);
        order3 = new Milk(order3);
        order3 = new Sugar(order3, 1);
        order3 = new SizeDecorator(order3, Size.MEDIUM);
        
        CoffeeOrder receipt3 = new CoffeeOrder(order3, "Charlie");
        receipt3.printReceipt();
        
        // Order 4: Build custom drink step by step
        System.out.println("=== Building Custom Drink ===\n");
        Beverage custom = new Espresso();
        System.out.println("Base: " + custom.getDescription() + " - $" + custom.cost());
        
        custom = new SizeDecorator(custom, Size.LARGE);
        System.out.println("+ Large: " + custom.getDescription() + " - $" + custom.cost());
        
        custom = new Milk(custom);
        System.out.println("+ Milk: " + custom.getDescription() + " - $" + custom.cost());
        
        custom = new WhippedCream(custom);
        System.out.println("+ Whipped Cream: " + custom.getDescription() + " - $" + custom.cost());
        
        custom = new Caramel(custom);
        System.out.println("+ Caramel: " + custom.getDescription() + " - $" + custom.cost());
        
        CoffeeOrder receipt4 = new CoffeeOrder(custom, "Diana");
        receipt4.printReceipt();
    }
}
```

### Key Points to Mention
- **Decorator Pattern**: Add responsibilities dynamically
- **Open/Closed Principle**: Open for extension, closed for modification
- **Recursive Cost Calculation**: Each decorator adds to wrapped object
- **Flexibility**: Can add any combination of decorators
- **Single Responsibility**: Each decorator has one job

---

*Continued in next file due to length...*

### Remaining Advanced Patterns

**17. Adapter Pattern - Payment Gateway Integration**  
**18. Facade Pattern - Home Automation System**

These additional patterns focus on structural design patterns crucial for system integration and API design interviews.
