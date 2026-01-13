# Java Machine Coding Round - Part 2 (Problems 6-12)

> **Target**: SDE2/SDE3 positions  
> **Duration**: Typically 60-90 minutes per question  
> **Focus**: Production-ready code with design patterns

---

## Table of Contents

6. [Library Management System](#6-library-management-system)
7. [Splitwise (Expense Sharing)](#7-splitwise-expense-sharing)
8. [URL Shortener](#8-url-shortener)

---

## 6. Library Management System

### Problem Statement
Design a library management system with:
- Add/remove books
- Borrow/return books
- Track due dates and fines
- Search books by title, author, ISBN
- Member management

### Interview Checklist

**Requirements:**
- [ ] Single copy or multiple copies per book?
- [ ] Fine calculation policy
- [ ] Reservation system needed?
- [ ] Member types (student, faculty)?
- [ ] Lending period (days)

**Design Decisions:**
- [ ] Book vs BookItem (one book, many copies)
- [ ] Strategy pattern for fine calculation
- [ ] Observer pattern for due date notifications
- [ ] Repository pattern for data access

**Implementation Focus:**
- [ ] Clear domain model
- [ ] Transaction tracking
- [ ] Date handling for due dates
- [ ] Search optimization

### Solution

```java
enum BookStatus {
    AVAILABLE, BORROWED, RESERVED, LOST
}

enum MemberType {
    STUDENT(14), FACULTY(30);
    
    private final int maxBorrowDays;
    
    MemberType(int days) {
        this.maxBorrowDays = days;
    }
    
    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }
}

// Value Object
class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final String publisher;
    
    public Book(String isbn, String title, String author, String publisher) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }
    
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}

// Entity (actual physical copy)
class BookItem {
    private final String barcode;
    private final Book book;
    private BookStatus status;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private String borrowedBy;
    
    public BookItem(String barcode, Book book) {
        this.barcode = barcode;
        this.book = book;
        this.status = BookStatus.AVAILABLE;
    }
    
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }
    
    public void borrow(String memberId, int days) {
        if (!isAvailable()) {
            throw new IllegalStateException("Book is not available");
        }
        this.status = BookStatus.BORROWED;
        this.borrowedBy = memberId;
        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(days);
    }
    
    public void returnBook() {
        this.status = BookStatus.AVAILABLE;
        this.borrowedBy = null;
        this.borrowedDate = null;
        this.dueDate = null;
    }
    
    public boolean isOverdue() {
        return status == BookStatus.BORROWED && 
               LocalDate.now().isAfter(dueDate);
    }
    
    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    public String getBarcode() { return barcode; }
    public Book getBook() { return book; }
    public BookStatus getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public String getBorrowedBy() { return borrowedBy; }
}

class Member {
    private final String memberId;
    private final String name;
    private final MemberType type;
    private final List<BookItem> borrowedBooks;
    private double fineAmount;
    
    public Member(String memberId, String name, MemberType type) {
        this.memberId = memberId;
        this.name = name;
        this.type = type;
        this.borrowedBooks = new ArrayList<>();
        this.fineAmount = 0.0;
    }
    
    public void borrowBook(BookItem bookItem) {
        borrowedBooks.add(bookItem);
    }
    
    public void returnBook(BookItem bookItem) {
        borrowedBooks.remove(bookItem);
    }
    
    public void addFine(double amount) {
        this.fineAmount += amount;
    }
    
    public void payFine(double amount) {
        if (amount > fineAmount) {
            throw new IllegalArgumentException("Amount exceeds fine");
        }
        this.fineAmount -= amount;
    }
    
    public boolean canBorrow() {
        return fineAmount == 0 && borrowedBooks.size() < 5;
    }
    
    public String getMemberId() { return memberId; }
    public MemberType getType() { return type; }
    public List<BookItem> getBorrowedBooks() { return new ArrayList<>(borrowedBooks); }
    public double getFineAmount() { return fineAmount; }
}

// Strategy Pattern for Fine Calculation
interface FineCalculator {
    double calculateFine(long overdueDays);
}

class StandardFineCalculator implements FineCalculator {
    private static final double FINE_PER_DAY = 5.0;
    
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * FINE_PER_DAY;
    }
}

class Library {
    private final Map<String, Book> books; // ISBN -> Book
    private final Map<String, BookItem> bookItems; // Barcode -> BookItem
    private final Map<String, Member> members; // MemberId -> Member
    private final FineCalculator fineCalculator;
    private final ReadWriteLock lock;
    
    public Library() {
        this.books = new ConcurrentHashMap<>();
        this.bookItems = new ConcurrentHashMap<>();
        this.members = new ConcurrentHashMap<>();
        this.fineCalculator = new StandardFineCalculator();
        this.lock = new ReentrantReadWriteLock();
    }
    
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }
    
    public void addBookItem(BookItem bookItem) {
        books.putIfAbsent(bookItem.getBook().getIsbn(), bookItem.getBook());
        bookItems.put(bookItem.getBarcode(), bookItem);
    }
    
    public void registerMember(Member member) {
        members.put(member.getMemberId(), member);
    }
    
    public BookItem borrowBook(String memberId, String barcode) {
        lock.writeLock().lock();
        try {
            Member member = members.get(memberId);
            if (member == null) {
                throw new IllegalArgumentException("Member not found");
            }
            
            if (!member.canBorrow()) {
                throw new IllegalStateException("Member cannot borrow (has fines or max books)");
            }
            
            BookItem bookItem = bookItems.get(barcode);
            if (bookItem == null) {
                throw new IllegalArgumentException("Book item not found");
            }
            
            bookItem.borrow(memberId, member.getType().getMaxBorrowDays());
            member.borrowBook(bookItem);
            
            System.out.println("Book borrowed: " + bookItem.getBook().getTitle() + 
                             " by " + memberId + ", due: " + bookItem.getDueDate());
            return bookItem;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public double returnBook(String memberId, String barcode) {
        lock.writeLock().lock();
        try {
            Member member = members.get(memberId);
            if (member == null) {
                throw new IllegalArgumentException("Member not found");
            }
            
            BookItem bookItem = bookItems.get(barcode);
            if (bookItem == null) {
                throw new IllegalArgumentException("Book item not found");
            }
            
            if (!memberId.equals(bookItem.getBorrowedBy())) {
                throw new IllegalStateException("Book not borrowed by this member");
            }
            
            double fine = 0.0;
            if (bookItem.isOverdue()) {
                long overdueDays = bookItem.getOverdueDays();
                fine = fineCalculator.calculateFine(overdueDays);
                member.addFine(fine);
                System.out.println("Book overdue by " + overdueDays + " days. Fine: ₹" + fine);
            }
            
            bookItem.returnBook();
            member.returnBook(bookItem);
            
            System.out.println("Book returned: " + bookItem.getBook().getTitle());
            return fine;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public List<BookItem> searchByTitle(String title) {
        return bookItems.values().stream()
            .filter(item -> item.getBook().getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<BookItem> searchByAuthor(String author) {
        return bookItems.values().stream()
            .filter(item -> item.getBook().getAuthor().toLowerCase().contains(author.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<BookItem> getAvailableBooks() {
        return bookItems.values().stream()
            .filter(BookItem::isAvailable)
            .collect(Collectors.toList());
    }
    
    public List<BookItem> getOverdueBooks() {
        return bookItems.values().stream()
            .filter(BookItem::isOverdue)
            .collect(Collectors.toList());
    }
}

// Demo
public class LibraryDemo {
    public static void main(String[] args) {
        Library library = new Library();
        
        // Add books
        Book book1 = new Book("ISBN001", "Effective Java", "Joshua Bloch", "Addison-Wesley");
        Book book2 = new Book("ISBN002", "Clean Code", "Robert Martin", "Prentice Hall");
        
        library.addBookItem(new BookItem("BC001", book1));
        library.addBookItem(new BookItem("BC002", book1)); // Another copy
        library.addBookItem(new BookItem("BC003", book2));
        
        // Register members
        Member student = new Member("M001", "Alice", MemberType.STUDENT);
        Member faculty = new Member("M002", "Bob", MemberType.FACULTY);
        
        library.registerMember(student);
        library.registerMember(faculty);
        
        // Borrow books
        library.borrowBook("M001", "BC001");
        library.borrowBook("M002", "BC003");
        
        // Search
        List<BookItem> javaBooks = library.searchByTitle("Java");
        System.out.println("\nBooks with 'Java': " + javaBooks.size());
        
        // Return book
        double fine = library.returnBook("M001", "BC001");
        System.out.println("Fine paid: ₹" + fine);
        
        // Available books
        System.out.println("\nAvailable books: " + library.getAvailableBooks().size());
    }
}
```

### Key Points to Mention
- **Domain Model**: Clear distinction between Book (concept) and BookItem (physical copy)
- **Transaction Management**: Thread-safe borrow/return operations
- **Fine Calculation**: Strategy pattern for flexible pricing
- **Search**: Stream-based filtering with case-insensitive matching

---

## 7. Splitwise (Expense Sharing)

### Problem Statement
Design an expense sharing application like Splitwise:
- Add expenses
- Split equally, by percentage, or exact amounts
- Track who owes whom
- Settle debts
- Simplify debts (minimize transactions)

### Interview Checklist

**Requirements:**
- [ ] Split types (equal, exact, percentage)
- [ ] Group expenses or just peer-to-peer?
- [ ] Currency handling
- [ ] Debt simplification needed?
- [ ] Multiple groups per user?

**Design Decisions:**
- [ ] Strategy pattern for split strategies
- [ ] Graph algorithms for debt simplification
- [ ] Balance sheet per user
- [ ] Transaction log

**Implementation Focus:**
- [ ] Accurate decimal calculations (BigDecimal)
- [ ] Debt settlement algorithm
- [ ] Clean split logic
- [ ] Balance tracking

### Solution

```java
enum SplitType {
    EQUAL, EXACT, PERCENTAGE
}

class User {
    private final String userId;
    private final String name;
    private final String email;
    
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
    
    public String getUserId() { return userId; }
    public String getName() { return name; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

// Strategy Pattern for Different Split Types
interface SplitStrategy {
    Map<User, BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants, 
                                         Map<User, BigDecimal> splitData);
}

class EqualSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants, 
                                                 Map<User, BigDecimal> splitData) {
        Map<User, BigDecimal> splits = new HashMap<>();
        BigDecimal perPerson = totalAmount.divide(
            BigDecimal.valueOf(participants.size()), 
            2, 
            RoundingMode.HALF_UP
        );
        
        for (User user : participants) {
            splits.put(user, perPerson);
        }
        return splits;
    }
}

class ExactSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants, 
                                                 Map<User, BigDecimal> splitData) {
        // Validate that splits add up to total
        BigDecimal sum = splitData.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (sum.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Splits don't add up to total amount");
        }
        
        return new HashMap<>(splitData);
    }
}

class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants, 
                                                 Map<User, BigDecimal> splitData) {
        // Validate percentages add up to 100
        BigDecimal totalPercentage = splitData.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new IllegalArgumentException("Percentages must add up to 100");
        }
        
        Map<User, BigDecimal> splits = new HashMap<>();
        for (Map.Entry<User, BigDecimal> entry : splitData.entrySet()) {
            BigDecimal amount = totalAmount
                .multiply(entry.getValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            splits.put(entry.getKey(), amount);
        }
        return splits;
    }
}

class Expense {
    private final String expenseId;
    private final String description;
    private final BigDecimal amount;
    private final User paidBy;
    private final Map<User, BigDecimal> splits;
    private final LocalDateTime createdAt;
    
    public Expense(String expenseId, String description, BigDecimal amount, 
                   User paidBy, Map<User, BigDecimal> splits) {
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splits = new HashMap<>(splits);
        this.createdAt = LocalDateTime.now();
    }
    
    public String getExpenseId() { return expenseId; }
    public User getPaidBy() { return paidBy; }
    public Map<User, BigDecimal> getSplits() { return new HashMap<>(splits); }
    public BigDecimal getAmount() { return amount; }
}

class Balance {
    // Tracks: user1 owes user2 X amount
    private final Map<String, Map<String, BigDecimal>> balances;
    
    public Balance() {
        this.balances = new ConcurrentHashMap<>();
    }
    
    public void addExpense(Expense expense) {
        User payer = expense.getPaidBy();
        
        for (Map.Entry<User, BigDecimal> split : expense.getSplits().entrySet()) {
            User borrower = split.getKey();
            BigDecimal amount = split.getValue();
            
            if (!borrower.equals(payer)) {
                updateBalance(borrower.getUserId(), payer.getUserId(), amount);
            }
        }
    }
    
    private void updateBalance(String borrowerId, String lenderId, BigDecimal amount) {
        balances.putIfAbsent(borrowerId, new ConcurrentHashMap<>());
        
        Map<String, BigDecimal> userBalances = balances.get(borrowerId);
        userBalances.merge(lenderId, amount, BigDecimal::add);
    }
    
    public void settleBalance(String borrowerId, String lenderId, BigDecimal amount) {
        if (!balances.containsKey(borrowerId)) {
            throw new IllegalArgumentException("No balance to settle");
        }
        
        Map<String, BigDecimal> userBalances = balances.get(borrowerId);
        BigDecimal currentBalance = userBalances.getOrDefault(lenderId, BigDecimal.ZERO);
        
        if (currentBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Settlement amount exceeds debt");
        }
        
        BigDecimal newBalance = currentBalance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            userBalances.remove(lenderId);
        } else {
            userBalances.put(lenderId, newBalance);
        }
    }
    
    public Map<String, BigDecimal> getBalancesForUser(String userId) {
        return new HashMap<>(balances.getOrDefault(userId, new HashMap<>()));
    }
    
    public void showAllBalances() {
        for (Map.Entry<String, Map<String, BigDecimal>> entry : balances.entrySet()) {
            String borrower = entry.getKey();
            for (Map.Entry<String, BigDecimal> debt : entry.getValue().entrySet()) {
                String lender = debt.getKey();
                BigDecimal amount = debt.getValue();
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println(borrower + " owes " + lender + ": ₹" + amount);
                }
            }
        }
    }
}

class Splitwise {
    private final Map<String, User> users;
    private final Map<String, Expense> expenses;
    private final Balance balance;
    private final Map<SplitType, SplitStrategy> strategies;
    private int expenseCounter;
    
    public Splitwise() {
        this.users = new ConcurrentHashMap<>();
        this.expenses = new ConcurrentHashMap<>();
        this.balance = new Balance();
        this.strategies = new HashMap<>();
        this.expenseCounter = 1;
        
        // Initialize strategies
        strategies.put(SplitType.EQUAL, new EqualSplitStrategy());
        strategies.put(SplitType.EXACT, new ExactSplitStrategy());
        strategies.put(SplitType.PERCENTAGE, new PercentageSplitStrategy());
    }
    
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }
    
    public Expense addExpense(String description, BigDecimal amount, String paidById, 
                             List<String> participantIds, SplitType splitType, 
                             Map<String, BigDecimal> splitData) {
        User paidBy = users.get(paidById);
        if (paidBy == null) {
            throw new IllegalArgumentException("Payer not found");
        }
        
        List<User> participants = participantIds.stream()
            .map(users::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("No valid participants");
        }
        
        // Convert splitData keys from userId to User
        Map<User, BigDecimal> userSplitData = new HashMap<>();
        if (splitData != null) {
            for (Map.Entry<String, BigDecimal> entry : splitData.entrySet()) {
                User user = users.get(entry.getKey());
                if (user != null) {
                    userSplitData.put(user, entry.getValue());
                }
            }
        }
        
        // Calculate splits using strategy
        SplitStrategy strategy = strategies.get(splitType);
        Map<User, BigDecimal> splits = strategy.calculateSplits(amount, participants, userSplitData);
        
        // Create expense
        String expenseId = "EXP" + expenseCounter++;
        Expense expense = new Expense(expenseId, description, amount, paidBy, splits);
        expenses.put(expenseId, expense);
        
        // Update balances
        balance.addExpense(expense);
        
        System.out.println("Expense added: " + description + " for ₹" + amount);
        return expense;
    }
    
    public void settleBalance(String borrowerId, String lenderId, BigDecimal amount) {
        balance.settleBalance(borrowerId, lenderId, amount);
        System.out.println("Settled: " + borrowerId + " paid " + lenderId + " ₹" + amount);
    }
    
    public void showBalances(String userId) {
        Map<String, BigDecimal> userBalances = balance.getBalancesForUser(userId);
        if (userBalances.isEmpty()) {
            System.out.println(userId + " has no outstanding balances");
        } else {
            System.out.println("Balances for " + userId + ":");
            userBalances.forEach((lender, amount) -> 
                System.out.println("  Owes " + lender + ": ₹" + amount));
        }
    }
    
    public void showAllBalances() {
        balance.showAllBalances();
    }
}

// Demo
public class SplitwiseDemo {
    public static void main(String[] args) {
        Splitwise splitwise = new Splitwise();
        
        // Add users
        User alice = new User("U1", "Alice", "alice@example.com");
        User bob = new User("U2", "Bob", "bob@example.com");
        User charlie = new User("U3", "Charlie", "charlie@example.com");
        
        splitwise.addUser(alice);
        splitwise.addUser(bob);
        splitwise.addUser(charlie);
        
        // Expense 1: Dinner split equally
        splitwise.addExpense(
            "Dinner", 
            BigDecimal.valueOf(300), 
            "U1", 
            List.of("U1", "U2", "U3"),
            SplitType.EQUAL,
            null
        );
        
        // Expense 2: Movie tickets with exact amounts
        Map<String, BigDecimal> exactSplits = Map.of(
            "U1", BigDecimal.valueOf(100),
            "U2", BigDecimal.valueOf(150),
            "U3", BigDecimal.valueOf(150)
        );
        splitwise.addExpense(
            "Movie", 
            BigDecimal.valueOf(400), 
            "U2", 
            List.of("U1", "U2", "U3"),
            SplitType.EXACT,
            exactSplits
        );
        
        // Show all balances
        System.out.println("\n--- All Balances ---");
        splitwise.showAllBalances();
        
        // Settle a balance
        System.out.println("\n--- Settling Balance ---");
        splitwise.settleBalance("U2", "U1", BigDecimal.valueOf(100));
        
        // Show balances after settlement
        System.out.println("\n--- After Settlement ---");
        splitwise.showAllBalances();
    }
}
```

### Key Points to Mention
- **BigDecimal**: Precise monetary calculations
- **Strategy Pattern**: Different split calculation strategies
- **Balance Tracking**: Efficient debt tracking with nested maps
- **Scalability**: Can easily extend with new split types

---

## 8. URL Shortener

### Problem Statement
Design a URL shortening service like bit.ly:
- Shorten long URLs to short codes
- Redirect short URLs to original URLs
- Track click statistics
- Custom aliases (optional)
- Expiration dates (optional)

### Interview Checklist

**Requirements:**
- [ ] Short URL length (6-8 characters)
- [ ] Character set (alphanumeric, case-sensitive)
- [ ] Collision handling
- [ ] Custom aliases allowed?
- [ ] Analytics needed (click count, referrers)?
- [ ] Expiration time?

**Design Decisions:**
- [ ] Base62 encoding vs random generation
- [ ] Hash collision resolution
- [ ] Database (in-memory vs persistent)
- [ ] Cache for hot URLs

**Implementation Focus:**
- [ ] URL validation
- [ ] Unique ID generation
- [ ] Thread-safe operations
- [ ] Analytics tracking

### Solution

```java
class URLMapping {
    private final String shortCode;
    private final String longUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final AtomicLong clickCount;
    
    public URLMapping(String shortCode, String longUrl, LocalDateTime expiresAt) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.clickCount = new AtomicLong(0);
    }
    
    public void incrementClick() {
        clickCount.incrementAndGet();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public String getShortCode() { return shortCode; }
    public String getLongUrl() { return longUrl; }
    public long getClickCount() { return clickCount.get(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

class URLShortener {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final String BASE_URL = "http://short.url/";
    
    private final Map<String, URLMapping> shortToLong; // shortCode -> URLMapping
    private final Map<String, String> longToShort; // longUrl -> shortCode
    private final AtomicLong counter;
    private final Random random;
    
    public URLShortener() {
        this.shortToLong = new ConcurrentHashMap<>();
        this.longToShort = new ConcurrentHashMap<>();
        this.counter = new AtomicLong(1);
        this.random = new Random();
    }
    
    // Shorten URL with auto-generated code
    public String shortenURL(String longUrl) {
        return shortenURL(longUrl, null, null);
    }
    
    // Shorten URL with custom alias and expiration
    public String shortenURL(String longUrl, String customAlias, LocalDateTime expiresAt) {
        if (!isValidURL(longUrl)) {
            throw new IllegalArgumentException("Invalid URL");
        }
        
        // Check if URL already exists
        if (longToShort.containsKey(longUrl)) {
            String existingCode = longToShort.get(longUrl);
            URLMapping existing = shortToLong.get(existingCode);
            if (!existing.isExpired()) {
                return BASE_URL + existingCode;
            }
        }
        
        String shortCode;
        if (customAlias != null && !customAlias.isEmpty()) {
            if (shortToLong.containsKey(customAlias)) {
                throw new IllegalArgumentException("Custom alias already exists");
            }
            shortCode = customAlias;
        } else {
            shortCode = generateShortCode();
        }
        
        URLMapping mapping = new URLMapping(shortCode, longUrl, expiresAt);
        shortToLong.put(shortCode, mapping);
        longToShort.put(longUrl, shortCode);
        
        return BASE_URL + shortCode;
    }
    
    // Expand short URL to original
    public String expandURL(String shortUrl) {
        String shortCode = extractShortCode(shortUrl);
        URLMapping mapping = shortToLong.get(shortCode);
        
        if (mapping == null) {
            throw new IllegalArgumentException("Short URL not found");
        }
        
        if (mapping.isExpired()) {
            shortToLong.remove(shortCode);
            longToShort.remove(mapping.getLongUrl());
            throw new IllegalArgumentException("Short URL has expired");
        }
        
        mapping.incrementClick();
        return mapping.getLongUrl();
    }
    
    // Get statistics for a short URL
    public URLMapping getStatistics(String shortCode) {
        return shortToLong.get(shortCode);
    }
    
    // Generate short code using Base62 encoding of counter
    private String generateShortCode() {
        long id = counter.getAndIncrement();
        return encodeBase62(id);
    }
    
    // Alternative: Generate random short code
    private String generateRandomShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(BASE62.length());
            sb.append(BASE62.charAt(index));
        }
        
        String code = sb.toString();
        // Handle collision
        if (shortToLong.containsKey(code)) {
            return generateRandomShortCode(); // Retry
        }
        return code;
    }
    
    // Base62 encoding
    private String encodeBase62(long num) {
        if (num == 0) return String.valueOf(BASE62.charAt(0));
        
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % 62);
            sb.append(BASE62.charAt(remainder));
            num /= 62;
        }
        
        // Pad to minimum length
        while (sb.length() < SHORT_CODE_LENGTH) {
            sb.append(BASE62.charAt(0));
        }
        
        return sb.reverse().toString();
    }
    
    // Base62 decoding
    private long decodeBase62(String code) {
        long num = 0;
        for (char c : code.toCharArray()) {
            num = num * 62 + BASE62.indexOf(c);
        }
        return num;
    }
    
    private String extractShortCode(String shortUrl) {
        return shortUrl.replace(BASE_URL, "");
    }
    
    private boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// Demo
public class URLShortenerDemo {
    public static void main(String[] args) {
        URLShortener shortener = new URLShortener();
        
        // Shorten URLs
        String url1 = "https://www.example.com/very/long/url/path/page.html";
        String shortUrl1 = shortener.shortenURL(url1);
        System.out.println("Original: " + url1);
        System.out.println("Shortened: " + shortUrl1);
        
        // Custom alias
        String url2 = "https://www.github.com/repository";
        String shortUrl2 = shortener.shortenURL(url2, "github", null);
        System.out.println("\nCustom alias: " + shortUrl2);
        
        // With expiration
        LocalDateTime expiry = LocalDateTime.now().plusDays(7);
        String url3 = "https://www.temporary.com/page";
        String shortUrl3 = shortener.shortenURL(url3, null, expiry);
        System.out.println("\nExpiring URL: " + shortUrl3);
        
        // Expand URLs
        System.out.println("\nExpanding:");
        System.out.println(shortener.expandURL(shortUrl1));
        System.out.println(shortener.expandURL(shortUrl2));
        
        // Statistics
        URLMapping stats = shortener.getStatistics("github");
        if (stats != null) {
            System.out.println("\nStatistics for 'github':");
            System.out.println("  Clicks: " + stats.getClickCount());
            System.out.println("  Created: " + stats.getCreatedAt());
        }
    }
}
```

### Key Points to Mention
- **Encoding**: Base62 for URL-safe characters (62^7 = 3.5 trillion combinations)
- **Collision Handling**: Counter-based (deterministic) vs random (with retry)
- **Thread Safety**: ConcurrentHashMap and AtomicLong
- **Analytics**: Click tracking with atomic counters
- **Expiration**: Time-based URL invalidation

---

*Due to length constraints, I'll create this as Part 2 with problems 6-8 implemented. Would you like me to continue with problems 9-12 in the same file or create a Part 3?*

### Remaining Problems Preview

**9. Elevator System**: Multi-elevator scheduling with direction optimization  
**10. Rate Limiter**: Token bucket and sliding window algorithms  
**11. In-Memory Database**: CRUD with indexing and transactions  
**12. Vending Machine**: State machine pattern for vending operations
