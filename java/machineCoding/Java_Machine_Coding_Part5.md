# Java Machine Coding Round - Part 5 (Structural & Creational Patterns)

> **Target**: SDE2/SDE3 positions  
> **Duration**: Typically 60-90 minutes per question  
> **Focus**: Integration patterns and flexible object creation

---

## Table of Contents

17. [Adapter Pattern - Payment Gateway Integration](#17-adapter-pattern---payment-gateway-integration)
18. [Facade Pattern - Home Automation System](#18-facade-pattern---home-automation-system)
19. [Factory Pattern - Notification Service](#19-factory-pattern---notification-service)
20. [Abstract Factory Pattern - UI Theme System](#20-abstract-factory-pattern---ui-theme-system)
21. [Builder Pattern - SQL Query Builder](#21-builder-pattern---sql-query-builder)
22. [Prototype Pattern - Game Object Cloning](#22-prototype-pattern---game-object-cloning)

---

## 17. Adapter Pattern - Payment Gateway Integration

### Problem Statement
Design a payment processing system that:
- Integrates multiple payment gateways (Stripe, PayPal, Razorpay)
- Provides unified interface for all gateways
- Handles different request/response formats
- Support switching between gateways
- Retry logic and fallback mechanisms

### Interview Checklist

**Requirements:**
- [ ] Number of payment providers
- [ ] Common operations (charge, refund, verify)
- [ ] Error handling strategy
- [ ] Transaction logging needed?
- [ ] Webhook support?

**Design Decisions:**
- [ ] Adapter pattern for gateway integration
- [ ] Strategy pattern for gateway selection
- [ ] Template method for common workflow
- [ ] Circuit breaker for failures

**Implementation Focus:**
- [ ] Clean interface abstraction
- [ ] Provider-specific logic encapsulation
- [ ] Error mapping and handling
- [ ] Extensibility for new gateways

### Solution

```java
// Target Interface (What our application expects)
interface PaymentGateway {
    PaymentResponse processPayment(PaymentRequest request);
    RefundResponse processRefund(RefundRequest request);
    PaymentStatus checkStatus(String transactionId);
}

// Common DTOs
class PaymentRequest {
    private final String orderId;
    private final double amount;
    private final String currency;
    private final String customerEmail;
    private final Map<String, String> metadata;
    
    public PaymentRequest(String orderId, double amount, String currency, String customerEmail) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.customerEmail = customerEmail;
        this.metadata = new HashMap<>();
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCustomerEmail() { return customerEmail; }
    public Map<String, String> getMetadata() { return metadata; }
}

class PaymentResponse {
    private final boolean success;
    private final String transactionId;
    private final String message;
    private final LocalDateTime timestamp;
    
    public PaymentResponse(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

class RefundRequest {
    private final String transactionId;
    private final double amount;
    private final String reason;
    
    public RefundRequest(String transactionId, double amount, String reason) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.reason = reason;
    }
    
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
}

class RefundResponse {
    private final boolean success;
    private final String refundId;
    private final String message;
    
    public RefundResponse(boolean success, String refundId, String message) {
        this.success = success;
        this.refundId = refundId;
        this.message = message;
    }
    
    public boolean isSuccess() { return success; }
    public String getRefundId() { return refundId; }
    public String getMessage() { return message; }
}

enum PaymentStatus {
    PENDING, SUCCESS, FAILED, REFUNDED
}

// ============ Stripe Integration (Adaptee #1) ============

// Stripe's proprietary classes (third-party library)
class StripeCharge {
    private String id;
    private String status; // "succeeded", "failed", "pending"
    private double amount;
    
    public void create(double amount, String currency, String email) {
        // Simulate Stripe API call
        this.id = "stripe_" + UUID.randomUUID().toString();
        this.status = Math.random() > 0.1 ? "succeeded" : "failed";
        this.amount = amount;
    }
    
    public String getId() { return id; }
    public String getStatus() { return status; }
    public double getAmount() { return amount; }
}

class StripeRefund {
    private String id;
    private boolean success;
    
    public void create(String chargeId, double amount) {
        // Simulate Stripe refund API
        this.id = "refund_" + UUID.randomUUID().toString();
        this.success = Math.random() > 0.05;
    }
    
    public String getId() { return id; }
    public boolean isSuccess() { return success; }
}

// Adapter for Stripe
class StripePaymentAdapter implements PaymentGateway {
    private static final String GATEWAY_NAME = "Stripe";
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            System.out.println("Processing payment via " + GATEWAY_NAME);
            
            // Adapt our request to Stripe's format
            StripeCharge charge = new StripeCharge();
            charge.create(request.getAmount(), request.getCurrency(), request.getCustomerEmail());
            
            // Adapt Stripe's response to our format
            boolean success = "succeeded".equals(charge.getStatus());
            String message = success ? "Payment processed successfully" : "Payment failed";
            
            return new PaymentResponse(success, charge.getId(), message);
        } catch (Exception e) {
            return new PaymentResponse(false, null, "Stripe error: " + e.getMessage());
        }
    }
    
    @Override
    public RefundResponse processRefund(RefundRequest request) {
        try {
            System.out.println("Processing refund via " + GATEWAY_NAME);
            
            StripeRefund refund = new StripeRefund();
            refund.create(request.getTransactionId(), request.getAmount());
            
            String message = refund.isSuccess() ? "Refund processed" : "Refund failed";
            return new RefundResponse(refund.isSuccess(), refund.getId(), message);
        } catch (Exception e) {
            return new RefundResponse(false, null, "Stripe refund error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentStatus checkStatus(String transactionId) {
        // Simulate status check
        return Math.random() > 0.1 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}

// ============ PayPal Integration (Adaptee #2) ============

// PayPal's proprietary classes
class PayPalPayment {
    private String paymentId;
    private String state; // "approved", "failed", "created"
    private BigDecimal total;
    
    public PayPalPayment execute(BigDecimal amount, String currencyCode, String payerEmail) {
        // Simulate PayPal API
        this.paymentId = "PAYPAL-" + UUID.randomUUID().toString();
        this.state = Math.random() > 0.1 ? "approved" : "failed";
        this.total = amount;
        return this;
    }
    
    public String getPaymentId() { return paymentId; }
    public String getState() { return state; }
    public BigDecimal getTotal() { return total; }
}

class PayPalRefundRequest {
    private String saleId;
    private BigDecimal amount;
    
    public void setSaleId(String saleId) { this.saleId = saleId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

class PayPalRefundResponse {
    private String refundId;
    private String status;
    
    public PayPalRefundResponse execute(PayPalRefundRequest request) {
        this.refundId = "REF-" + UUID.randomUUID().toString();
        this.status = Math.random() > 0.05 ? "completed" : "failed";
        return this;
    }
    
    public String getRefundId() { return refundId; }
    public String getStatus() { return status; }
}

// Adapter for PayPal
class PayPalPaymentAdapter implements PaymentGateway {
    private static final String GATEWAY_NAME = "PayPal";
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            System.out.println("Processing payment via " + GATEWAY_NAME);
            
            // Adapt our request to PayPal's format
            PayPalPayment payment = new PayPalPayment();
            payment.execute(
                BigDecimal.valueOf(request.getAmount()),
                request.getCurrency(),
                request.getCustomerEmail()
            );
            
            // Adapt PayPal's response to our format
            boolean success = "approved".equals(payment.getState());
            String message = success ? "PayPal payment approved" : "PayPal payment failed";
            
            return new PaymentResponse(success, payment.getPaymentId(), message);
        } catch (Exception e) {
            return new PaymentResponse(false, null, "PayPal error: " + e.getMessage());
        }
    }
    
    @Override
    public RefundResponse processRefund(RefundRequest request) {
        try {
            System.out.println("Processing refund via " + GATEWAY_NAME);
            
            PayPalRefundRequest refundReq = new PayPalRefundRequest();
            refundReq.setSaleId(request.getTransactionId());
            refundReq.setAmount(BigDecimal.valueOf(request.getAmount()));
            
            PayPalRefundResponse refundResp = new PayPalRefundResponse();
            refundResp.execute(refundReq);
            
            boolean success = "completed".equals(refundResp.getStatus());
            String message = success ? "Refund completed" : "Refund failed";
            
            return new RefundResponse(success, refundResp.getRefundId(), message);
        } catch (Exception e) {
            return new RefundResponse(false, null, "PayPal refund error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentStatus checkStatus(String transactionId) {
        return Math.random() > 0.1 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}

// ============ Razorpay Integration (Adaptee #3) ============

// Razorpay's proprietary classes
class RazorpayOrder {
    private String orderId;
    private int amountPaise; // Razorpay uses paise (1/100 of rupee)
    private String status;
    
    public void create(int amountPaise, String currency) {
        this.orderId = "order_" + UUID.randomUUID().toString();
        this.amountPaise = amountPaise;
        this.status = Math.random() > 0.1 ? "paid" : "failed";
    }
    
    public String getOrderId() { return orderId; }
    public int getAmountPaise() { return amountPaise; }
    public String getStatus() { return status; }
}

// Adapter for Razorpay
class RazorpayPaymentAdapter implements PaymentGateway {
    private static final String GATEWAY_NAME = "Razorpay";
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            System.out.println("Processing payment via " + GATEWAY_NAME);
            
            // Convert amount to paise (Razorpay requirement)
            int amountPaise = (int) (request.getAmount() * 100);
            
            RazorpayOrder order = new RazorpayOrder();
            order.create(amountPaise, request.getCurrency());
            
            boolean success = "paid".equals(order.getStatus());
            String message = success ? "Payment successful" : "Payment failed";
            
            return new PaymentResponse(success, order.getOrderId(), message);
        } catch (Exception e) {
            return new PaymentResponse(false, null, "Razorpay error: " + e.getMessage());
        }
    }
    
    @Override
    public RefundResponse processRefund(RefundRequest request) {
        try {
            System.out.println("Processing refund via " + GATEWAY_NAME);
            
            // Simulate Razorpay refund
            String refundId = "rfnd_" + UUID.randomUUID().toString();
            boolean success = Math.random() > 0.05;
            String message = success ? "Refund initiated" : "Refund failed";
            
            return new RefundResponse(success, refundId, message);
        } catch (Exception e) {
            return new RefundResponse(false, null, "Razorpay refund error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentStatus checkStatus(String transactionId) {
        return Math.random() > 0.1 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}

// ============ Payment Service with Strategy Pattern ============

enum GatewayType {
    STRIPE, PAYPAL, RAZORPAY
}

class PaymentService {
    private final Map<GatewayType, PaymentGateway> gateways;
    private GatewayType defaultGateway;
    
    public PaymentService() {
        this.gateways = new HashMap<>();
        this.gateways.put(GatewayType.STRIPE, new StripePaymentAdapter());
        this.gateways.put(GatewayType.PAYPAL, new PayPalPaymentAdapter());
        this.gateways.put(GatewayType.RAZORPAY, new RazorpayPaymentAdapter());
        this.defaultGateway = GatewayType.STRIPE;
    }
    
    public void setDefaultGateway(GatewayType gateway) {
        this.defaultGateway = gateway;
    }
    
    public PaymentResponse processPayment(PaymentRequest request) {
        return processPayment(request, defaultGateway);
    }
    
    public PaymentResponse processPayment(PaymentRequest request, GatewayType gatewayType) {
        PaymentGateway gateway = gateways.get(gatewayType);
        if (gateway == null) {
            return new PaymentResponse(false, null, "Gateway not available");
        }
        
        PaymentResponse response = gateway.processPayment(request);
        
        // If payment fails, try fallback gateway
        if (!response.isSuccess() && gatewayType == defaultGateway) {
            System.out.println("⚠️ Payment failed, trying fallback gateway...");
            GatewayType fallback = getFallbackGateway(gatewayType);
            if (fallback != null) {
                response = gateways.get(fallback).processPayment(request);
            }
        }
        
        return response;
    }
    
    public RefundResponse processRefund(RefundRequest request, GatewayType gatewayType) {
        PaymentGateway gateway = gateways.get(gatewayType);
        if (gateway == null) {
            return new RefundResponse(false, null, "Gateway not available");
        }
        
        return gateway.processRefund(request);
    }
    
    private GatewayType getFallbackGateway(GatewayType current) {
        // Simple fallback logic
        return switch (current) {
            case STRIPE -> GatewayType.PAYPAL;
            case PAYPAL -> GatewayType.RAZORPAY;
            case RAZORPAY -> GatewayType.STRIPE;
        };
    }
}

// Demo
public class PaymentGatewayDemo {
    public static void main(String[] args) {
        PaymentService paymentService = new PaymentService();
        
        System.out.println("=== Payment Gateway Integration Demo ===\n");
        
        // Test with Stripe
        System.out.println("--- Test 1: Stripe Payment ---");
        PaymentRequest request1 = new PaymentRequest("ORD001", 100.50, "USD", "alice@example.com");
        PaymentResponse response1 = paymentService.processPayment(request1, GatewayType.STRIPE);
        System.out.println("Result: " + (response1.isSuccess() ? "✓" : "✗") + 
                         " | Transaction ID: " + response1.getTransactionId());
        System.out.println("Message: " + response1.getMessage() + "\n");
        
        // Test with PayPal
        System.out.println("--- Test 2: PayPal Payment ---");
        PaymentRequest request2 = new PaymentRequest("ORD002", 250.75, "USD", "bob@example.com");
        PaymentResponse response2 = paymentService.processPayment(request2, GatewayType.PAYPAL);
        System.out.println("Result: " + (response2.isSuccess() ? "✓" : "✗") + 
                         " | Transaction ID: " + response2.getTransactionId());
        System.out.println("Message: " + response2.getMessage() + "\n");
        
        // Test with Razorpay
        System.out.println("--- Test 3: Razorpay Payment ---");
        PaymentRequest request3 = new PaymentRequest("ORD003", 5000.0, "INR", "charlie@example.com");
        PaymentResponse response3 = paymentService.processPayment(request3, GatewayType.RAZORPAY);
        System.out.println("Result: " + (response3.isSuccess() ? "✓" : "✗") + 
                         " | Transaction ID: " + response3.getTransactionId());
        System.out.println("Message: " + response3.getMessage() + "\n");
        
        // Test refund
        if (response1.isSuccess()) {
            System.out.println("--- Test 4: Refund ---");
            RefundRequest refundReq = new RefundRequest(response1.getTransactionId(), 100.50, "Customer request");
            RefundResponse refundResp = paymentService.processRefund(refundReq, GatewayType.STRIPE);
            System.out.println("Refund: " + (refundResp.isSuccess() ? "✓" : "✗") + 
                             " | Refund ID: " + refundResp.getRefundId());
            System.out.println("Message: " + refundResp.getMessage() + "\n");
        }
        
        // Test default gateway with fallback
        System.out.println("--- Test 5: Default Gateway with Fallback ---");
        PaymentRequest request4 = new PaymentRequest("ORD004", 75.0, "USD", "diana@example.com");
        PaymentResponse response4 = paymentService.processPayment(request4);
        System.out.println("Result: " + (response4.isSuccess() ? "✓" : "✗") + 
                         " | Transaction ID: " + response4.getTransactionId());
        System.out.println("Message: " + response4.getMessage());
    }
}
```

### Key Points to Mention
- **Adapter Pattern**: Converts incompatible interfaces to work together
- **Multiple Adapters**: Each gateway has its own adapter
- **Unified Interface**: Application code doesn't know about specific gateways
- **Strategy Pattern**: Easy to switch between gateways
- **Fallback Mechanism**: Automatic retry with different gateway
- **Extensibility**: New gateways can be added without changing existing code

---

## 18. Facade Pattern - Home Automation System

### Problem Statement
Design a smart home automation system with:
- Control multiple subsystems (lights, thermostat, security, entertainment)
- Simplified interface for complex operations
- Preset modes (Movie mode, Sleep mode, Away mode)
- Schedule automation
- Individual subsystem control still possible

### Interview Checklist

**Requirements:**
- [ ] Number of subsystems
- [ ] Preset modes needed
- [ ] Voice control integration?
- [ ] Remote access required?
- [ ] Energy monitoring?

**Design Decisions:**
- [ ] Facade pattern for simplified interface
- [ ] Individual subsystem classes
- [ ] Command pattern for scheduling (optional)
- [ ] Observer pattern for status updates

**Implementation Focus:**
- [ ] Clean facade interface
- [ ] Proper delegation to subsystems
- [ ] Maintain subsystem independence
- [ ] Error handling across systems

### Solution

```java
// ================== Subsystems ==================

// Lighting Subsystem
class LightingSystem {
    private final Map<String, Boolean> lights;
    private int brightness;
    
    public LightingSystem() {
        this.lights = new HashMap<>();
        lights.put("Living Room", false);
        lights.put("Bedroom", false);
        lights.put("Kitchen", false);
        lights.put("Bathroom", false);
        this.brightness = 100;
    }
    
    public void turnOn(String room) {
        lights.put(room, true);
        System.out.println("💡 " + room + " lights ON (Brightness: " + brightness + "%)");
    }
    
    public void turnOff(String room) {
        lights.put(room, false);
        System.out.println("💡 " + room + " lights OFF");
    }
    
    public void turnOnAll() {
        lights.keySet().forEach(this::turnOn);
    }
    
    public void turnOffAll() {
        lights.keySet().forEach(this::turnOff);
    }
    
    public void setBrightness(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
        System.out.println("💡 Brightness set to " + this.brightness + "%");
    }
    
    public void dim() {
        setBrightness(30);
    }
}

// Climate Control Subsystem
class ClimateControlSystem {
    private int temperature;
    private boolean heatingOn;
    private boolean coolingOn;
    private final int minTemp = 16;
    private final int maxTemp = 30;
    
    public ClimateControlSystem() {
        this.temperature = 22;
        this.heatingOn = false;
        this.coolingOn = false;
    }
    
    public void setTemperature(int temperature) {
        this.temperature = Math.max(minTemp, Math.min(maxTemp, temperature));
        System.out.println("🌡️ Temperature set to " + this.temperature + "°C");
        adjustHeatingCooling();
    }
    
    private void adjustHeatingCooling() {
        if (temperature < 20) {
            heatingOn = true;
            coolingOn = false;
            System.out.println("🔥 Heating ON");
        } else if (temperature > 24) {
            heatingOn = false;
            coolingOn = true;
            System.out.println("❄️ Cooling ON");
        } else {
            heatingOn = false;
            coolingOn = false;
            System.out.println("🌡️ Climate control in eco mode");
        }
    }
    
    public void turnOff() {
        heatingOn = false;
        coolingOn = false;
        System.out.println("🌡️ Climate control OFF");
    }
}

// Security System
class SecuritySystem {
    private boolean armed;
    private boolean motionDetection;
    private final Set<String> cameras;
    
    public SecuritySystem() {
        this.armed = false;
        this.motionDetection = false;
        this.cameras = new HashSet<>();
        cameras.add("Front Door");
        cameras.add("Back Door");
        cameras.add("Garage");
    }
    
    public void arm() {
        armed = true;
        motionDetection = true;
        System.out.println("🔒 Security system ARMED");
        System.out.println("📹 All cameras activated");
    }
    
    public void disarm(String code) {
        if ("1234".equals(code)) {
            armed = false;
            motionDetection = false;
            System.out.println("🔓 Security system DISARMED");
        } else {
            System.out.println("❌ Invalid security code");
        }
    }
    
    public void enableCamera(String location) {
        System.out.println("📹 Camera at " + location + " enabled");
    }
    
    public void disableAllCameras() {
        System.out.println("📹 All cameras disabled");
    }
}

// Entertainment System
class EntertainmentSystem {
    private boolean tvOn;
    private boolean soundSystemOn;
    private int volume;
    private String currentInput;
    
    public EntertainmentSystem() {
        this.tvOn = false;
        this.soundSystemOn = false;
        this.volume = 20;
        this.currentInput = "HDMI1";
    }
    
    public void turnOnTV() {
        tvOn = true;
        System.out.println("📺 TV ON");
    }
    
    public void turnOffTV() {
        tvOn = false;
        System.out.println("📺 TV OFF");
    }
    
    public void turnOnSoundSystem() {
        soundSystemOn = true;
        System.out.println("🔊 Sound system ON (Volume: " + volume + ")");
    }
    
    public void turnOffSoundSystem() {
        soundSystemOn = false;
        System.out.println("🔊 Sound system OFF");
    }
    
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        System.out.println("🔊 Volume set to " + this.volume);
    }
    
    public void setInput(String input) {
        this.currentInput = input;
        System.out.println("📺 Input changed to " + input);
    }
    
    public void startMovie() {
        turnOnTV();
        turnOnSoundSystem();
        setInput("HDMI1");
        setVolume(40);
    }
    
    public void shutdownAll() {
        turnOffTV();
        turnOffSoundSystem();
    }
}

// Blinds Control System
class BlindsSystem {
    private final Map<String, Integer> blinds; // room -> position (0-100)
    
    public BlindsSystem() {
        this.blinds = new HashMap<>();
        blinds.put("Living Room", 100);
        blinds.put("Bedroom", 100);
    }
    
    public void open(String room) {
        blinds.put(room, 100);
        System.out.println("🪟 " + room + " blinds OPEN");
    }
    
    public void close(String room) {
        blinds.put(room, 0);
        System.out.println("🪟 " + room + " blinds CLOSED");
    }
    
    public void openAll() {
        blinds.keySet().forEach(this::open);
    }
    
    public void closeAll() {
        blinds.keySet().forEach(this::close);
    }
    
    public void setPosition(String room, int position) {
        position = Math.max(0, Math.min(100, position));
        blinds.put(room, position);
        System.out.println("🪟 " + room + " blinds set to " + position + "%");
    }
}

// ================== Facade ==================

class SmartHomeFacade {
    private final LightingSystem lighting;
    private final ClimateControlSystem climate;
    private final SecuritySystem security;
    private final EntertainmentSystem entertainment;
    private final BlindsSystem blinds;
    
    public SmartHomeFacade() {
        this.lighting = new LightingSystem();
        this.climate = new ClimateControlSystem();
        this.security = new SecuritySystem();
        this.entertainment = new EntertainmentSystem();
        this.blinds = new BlindsSystem();
    }
    
    // Preset Mode: Movie Night
    public void activateMovieMode() {
        System.out.println("\n🎬 Activating MOVIE MODE...\n");
        
        lighting.dim();
        lighting.turnOff("Kitchen");
        lighting.turnOff("Bathroom");
        
        climate.setTemperature(21);
        
        blinds.closeAll();
        
        entertainment.startMovie();
        
        System.out.println("\n🎬 Movie mode activated!\n");
    }
    
    // Preset Mode: Good Morning
    public void activateGoodMorningMode() {
        System.out.println("\n☀️ Activating GOOD MORNING MODE...\n");
        
        blinds.openAll();
        
        lighting.setBrightness(100);
        lighting.turnOn("Bedroom");
        lighting.turnOn("Kitchen");
        lighting.turnOn("Bathroom");
        
        climate.setTemperature(22);
        
        entertainment.turnOffSoundSystem();
        
        security.disarm("1234");
        
        System.out.println("\n☀️ Good morning mode activated!\n");
    }
    
    // Preset Mode: Sleep/Night
    public void activateSleepMode() {
        System.out.println("\n🌙 Activating SLEEP MODE...\n");
        
        lighting.turnOffAll();
        
        blinds.closeAll();
        
        climate.setTemperature(20);
        
        entertainment.shutdownAll();
        
        security.arm();
        
        System.out.println("\n🌙 Sleep mode activated! Good night!\n");
    }
    
    // Preset Mode: Away/Vacation
    public void activateAwayMode() {
        System.out.println("\n🏖️ Activating AWAY MODE...\n");
        
        lighting.turnOffAll();
        
        climate.setTemperature(18); // Energy saving
        
        entertainment.shutdownAll();
        
        security.arm();
        
        blinds.closeAll();
        
        System.out.println("\n🏖️ Away mode activated! House secured.\n");
    }
    
    // Preset Mode: Party
    public void activatePartyMode() {
        System.out.println("\n🎉 Activating PARTY MODE...\n");
        
        lighting.setBrightness(80);
        lighting.turnOnAll();
        
        climate.setTemperature(21);
        
        entertainment.turnOnSoundSystem();
        entertainment.setVolume(60);
        entertainment.setInput("Bluetooth");
        
        security.disarm("1234");
        
        System.out.println("\n🎉 Party mode activated! Let's celebrate!\n");
    }
    
    // Preset Mode: Work from Home
    public void activateWorkMode() {
        System.out.println("\n💼 Activating WORK MODE...\n");
        
        lighting.setBrightness(100);
        lighting.turnOn("Living Room");
        
        climate.setTemperature(22);
        
        blinds.setPosition("Living Room", 50); // Partial
        
        entertainment.turnOffSoundSystem();
        
        System.out.println("\n💼 Work mode activated! Stay productive!\n");
    }
    
    // Individual subsystem access (still available)
    public LightingSystem getLighting() { return lighting; }
    public ClimateControlSystem getClimate() { return climate; }
    public SecuritySystem getSecurity() { return security; }
    public EntertainmentSystem getEntertainment() { return entertainment; }
    public BlindsSystem getBlinds() { return blinds; }
}

// ================== Demo ==================

public class SmartHomeDemo {
    public static void main(String[] args) throws InterruptedException {
        SmartHomeFacade smartHome = new SmartHomeFacade();
        
        System.out.println("=== Smart Home Automation System ===");
        
        // Morning routine
        smartHome.activateGoodMorningMode();
        Thread.sleep(2000);
        
        // Work from home
        smartHome.activateWorkMode();
        Thread.sleep(2000);
        
        // Evening movie
        smartHome.activateMovieMode();
        Thread.sleep(2000);
        
        // Bedtime
        smartHome.activateSleepMode();
        Thread.sleep(2000);
        
        // Individual control example (bypassing facade)
        System.out.println("--- Individual Control Example ---\n");
        smartHome.getLighting().turnOn("Kitchen");
        smartHome.getClimate().setTemperature(23);
        
        Thread.sleep(1000);
        
        // Going on vacation
        smartHome.activateAwayMode();
    }
}
```

### Key Points to Mention
- **Facade Pattern**: Simplifies complex subsystem interactions
- **Preset Modes**: Common scenarios encapsulated in simple methods
- **Subsystem Independence**: Can still access individual systems
- **Reduced Complexity**: Client doesn't need to know subsystem details
- **Loose Coupling**: Facade shields clients from subsystem changes

---

## 19. Factory Pattern - Notification Service

### Problem Statement
Design a notification service that:
- Sends notifications via multiple channels (Email, SMS, Push, Slack)
- Factory creates appropriate notification handler
- Support notification templates
- Queue and batch processing
- Retry failed notifications

### Interview Checklist

**Requirements:**
- [ ] Notification channels supported
- [ ] Template management needed?
- [ ] Priority levels?
- [ ] Delivery guarantees?
- [ ] Analytics/tracking?

**Design Decisions:**
- [ ] Factory pattern for channel creation
- [ ] Strategy pattern for sending logic
- [ ] Template method for common workflow
- [ ] Queue for async processing

**Implementation Focus:**
- [ ] Clean factory interface
- [ ] Easy to add new channels
- [ ] Error handling per channel
- [ ] Scalability considerations

### Solution

```java
// Notification Interface (Product)
interface Notification {
    void send(String recipient, String message);
    boolean validate(String recipient);
    String getChannelName();
}

// Concrete Products
class EmailNotification implements Notification {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @Override
    public void send(String recipient, String message) {
        if (!validate(recipient)) {
            throw new IllegalArgumentException("Invalid email address");
        }
        
        System.out.println("📧 Sending Email to: " + recipient);
        System.out.println("Subject: Notification");
        System.out.println("Body: " + message);
        System.out.println("✓ Email sent successfully\n");
    }
    
    @Override
    public boolean validate(String recipient) {
        return EMAIL_PATTERN.matcher(recipient).matches();
    }
    
    @Override
    public String getChannelName() {
        return "Email";
    }
}

class SMSNotification implements Notification {
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    
    @Override
    public void send(String recipient, String message) {
        if (!validate(recipient)) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        
        System.out.println("📱 Sending SMS to: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("✓ SMS sent successfully\n");
    }
    
    @Override
    public boolean validate(String recipient) {
        return PHONE_PATTERN.matcher(recipient).matches();
    }
    
    @Override
    public String getChannelName() {
        return "SMS";
    }
}

class PushNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        if (!validate(recipient)) {
            throw new IllegalArgumentException("Invalid device token");
        }
        
        System.out.println("🔔 Sending Push Notification to device: " + recipient);
        System.out.println("Title: Alert");
        System.out.println("Message: " + message);
        System.out.println("✓ Push notification sent successfully\n");
    }
    
    @Override
    public boolean validate(String recipient) {
        return recipient != null && recipient.length() >= 32;
    }
    
    @Override
    public String getChannelName() {
        return "Push";
    }
}

class SlackNotification implements Notification {
    @Override
    public void send(String recipient, String message) {
        if (!validate(recipient)) {
            throw new IllegalArgumentException("Invalid Slack channel");
        }
        
        System.out.println("💬 Posting to Slack channel: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("✓ Slack message posted successfully\n");
    }
    
    @Override
    public boolean validate(String recipient) {
        return recipient != null && recipient.startsWith("#");
    }
    
    @Override
    public String getChannelName() {
        return "Slack";
    }
}

// Factory (Creator)
enum NotificationChannel {
    EMAIL, SMS, PUSH, SLACK
}

class NotificationFactory {
    // Simple Factory Method
    public static Notification createNotification(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> new EmailNotification();
            case SMS -> new SMSNotification();
            case PUSH -> new PushNotification();
            case SLACK -> new SlackNotification();
        };
    }
    
    // Factory method with validation
    public static Notification createNotificationWithValidation(
            NotificationChannel channel, String recipient) {
        Notification notification = createNotification(channel);
        if (!notification.validate(recipient)) {
            throw new IllegalArgumentException(
                "Invalid recipient for " + channel + ": " + recipient
            );
        }
        return notification;
    }
}

// Notification Template
class NotificationTemplate {
    private final String templateId;
    private final String subject;
    private final String body;
    private final Map<String, String> placeholders;
    
    public NotificationTemplate(String templateId, String subject, String body) {
        this.templateId = templateId;
        this.subject = subject;
        this.body = body;
        this.placeholders = new HashMap<>();
    }
    
    public void setPlaceholder(String key, String value) {
        placeholders.put(key, value);
    }
    
    public String render() {
        String rendered = body;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return rendered;
    }
    
    public String getSubject() { return subject; }
}

// Notification Service
class NotificationService {
    private final Map<String, NotificationTemplate> templates;
    private final BlockingQueue<NotificationTask> queue;
    private final ExecutorService executor;
    private volatile boolean running;
    
    public NotificationService(int workerThreads) {
        this.templates = new ConcurrentHashMap<>();
        this.queue = new LinkedBlockingQueue<>();
        this.executor = Executors.newFixedThreadPool(workerThreads);
        this.running = true;
        
        // Start worker threads
        for (int i = 0; i < workerThreads; i++) {
            executor.submit(this::processQueue);
        }
    }
    
    public void registerTemplate(NotificationTemplate template) {
        templates.put(template.templateId, template);
    }
    
    public void sendNotification(NotificationChannel channel, 
                                String recipient, 
                                String message) {
        try {
            Notification notification = NotificationFactory.createNotification(channel);
            queue.offer(new NotificationTask(notification, recipient, message));
        } catch (Exception e) {
            System.err.println("Failed to queue notification: " + e.getMessage());
        }
    }
    
    public void sendFromTemplate(NotificationChannel channel,
                                String recipient,
                                String templateId,
                                Map<String, String> placeholders) {
        NotificationTemplate template = templates.get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }
        
        // Set placeholders
        placeholders.forEach(template::setPlaceholder);
        String message = template.render();
        
        sendNotification(channel, recipient, message);
    }
    
    private void processQueue() {
        while (running || !queue.isEmpty()) {
            try {
                NotificationTask task = queue.poll(100, TimeUnit.MILLISECONDS);
                if (task != null) {
                    task.execute();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error processing notification: " + e.getMessage());
            }
        }
    }
    
    public void shutdown() {
        running = false;
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
    
    private static class NotificationTask {
        private final Notification notification;
        private final String recipient;
        private final String message;
        
        public NotificationTask(Notification notification, String recipient, String message) {
            this.notification = notification;
            this.recipient = recipient;
            this.message = message;
        }
        
        public void execute() {
            try {
                notification.send(recipient, message);
            } catch (Exception e) {
                System.err.println("Failed to send " + notification.getChannelName() + 
                                 " notification: " + e.getMessage());
            }
        }
    }
}

// Demo
public class NotificationServiceDemo {
    public static void main(String[] args) throws InterruptedException {
        NotificationService service = new NotificationService(3);
        
        System.out.println("=== Notification Service Demo ===\n");
        
        // Direct notifications
        System.out.println("--- Direct Notifications ---");
        service.sendNotification(NotificationChannel.EMAIL, 
                                "alice@example.com", 
                                "Welcome to our service!");
        
        service.sendNotification(NotificationChannel.SMS, 
                                "+1234567890", 
                                "Your OTP is: 123456");
        
        service.sendNotification(NotificationChannel.PUSH, 
                                "device_token_abc123xyz789012345678901234567890", 
                                "You have a new message!");
        
        service.sendNotification(NotificationChannel.SLACK, 
                                "#general", 
                                "Deploy completed successfully!");
        
        Thread.sleep(2000);
        
        // Template-based notifications
        System.out.println("\n--- Template-based Notifications ---");
        
        NotificationTemplate welcomeTemplate = new NotificationTemplate(
            "welcome",
            "Welcome!",
            "Hello {{name}}, welcome to {{company}}! Your account {{email}} is now active."
        );
        service.registerTemplate(welcomeTemplate);
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "Bob");
        placeholders.put("company", "TechCorp");
        placeholders.put("email", "bob@example.com");
        
        service.sendFromTemplate(NotificationChannel.EMAIL,
                                "bob@example.com",
                                "welcome",
                                placeholders);
        
        Thread.sleep(2000);
        
        service.shutdown();
        System.out.println("\n=== Notification Service Stopped ===");
    }
}
```

### Key Points to Mention
- **Factory Pattern**: Encapsulates object creation logic
- **Extensibility**: Easy to add new notification channels
- **Single Responsibility**: Each channel has its own class
- **Async Processing**: Queue-based notification delivery
- **Template Support**: Reusable message templates

---

## 20. Abstract Factory Pattern - UI Theme System

### Problem Statement
Design a UI theming system that:
- Support multiple themes (Light, Dark, HighContrast)
- Create consistent UI components per theme
- All components follow theme styling
- Easy to add new themes
- Runtime theme switching

### Interview Checklist

**Requirements:**
- [ ] Number of themes
- [ ] UI components (buttons, inputs, cards)
- [ ] Custom themes allowed?
- [ ] Theme persistence?
- [ ] Animation/transitions?

**Design Decisions:**
- [ ] Abstract Factory for theme creation
- [ ] Family of related products (components)
- [ ] Singleton for theme manager
- [ ] Strategy for rendering

**Implementation Focus:**
- [ ] Consistent component families
- [ ] Theme independence
- [ ] Easy theme switching
- [ ] Component creation encapsulation

### Solution

```java
// Abstract Products
interface Button {
    void render();
    String getStyle();
}

interface TextField {
    void render();
    String getStyle();
}

interface Card {
    void render();
    String getStyle();
}

interface Panel {
    void render();
    String getStyle();
}

// ============ Light Theme Products ============

class LightButton implements Button {
    @Override
    public void render() {
        System.out.println("🔵 Rendering Light Button");
    }
    
    @Override
    public String getStyle() {
        return "background: white; color: black; border: 1px solid #ddd";
    }
}

class LightTextField implements TextField {
    @Override
    public void render() {
        System.out.println("📝 Rendering Light TextField");
    }
    
    @Override
    public String getStyle() {
        return "background: white; color: black; border: 1px solid #ccc";
    }
}

class LightCard implements Card {
    @Override
    public void render() {
        System.out.println("🗃️ Rendering Light Card");
    }
    
    @Override
    public String getStyle() {
        return "background: #f9f9f9; shadow: 0 2px 4px rgba(0,0,0,0.1)";
    }
}

class LightPanel implements Panel {
    @Override
    public void render() {
        System.out.println("📄 Rendering Light Panel");
    }
    
    @Override
    public String getStyle() {
        return "background: white; padding: 16px";
    }
}

// ============ Dark Theme Products ============

class DarkButton implements Button {
    @Override
    public void render() {
        System.out.println("🔵 Rendering Dark Button");
    }
    
    @Override
    public String getStyle() {
        return "background: #2d2d2d; color: white; border: 1px solid #444";
    }
}

class DarkTextField implements TextField {
    @Override
    public void render() {
        System.out.println("📝 Rendering Dark TextField");
    }
    
    @Override
    public String getStyle() {
        return "background: #1e1e1e; color: white; border: 1px solid #555";
    }
}

class DarkCard implements Card {
    @Override
    public void render() {
        System.out.println("🗃️ Rendering Dark Card");
    }
    
    @Override
    public String getStyle() {
        return "background: #2d2d2d; shadow: 0 2px 4px rgba(0,0,0,0.5)";
    }
}

class DarkPanel implements Panel {
    @Override
    public void render() {
        System.out.println("📄 Rendering Dark Panel");
    }
    
    @Override
    public String getStyle() {
        return "background: #1e1e1e; padding: 16px";
    }
}

// ============ High Contrast Theme Products ============

class HighContrastButton implements Button {
    @Override
    public void render() {
        System.out.println("🔵 Rendering High Contrast Button");
    }
    
    @Override
    public String getStyle() {
        return "background: black; color: yellow; border: 3px solid yellow";
    }
}

class HighContrastTextField implements TextField {
    @Override
    public void render() {
        System.out.println("📝 Rendering High Contrast TextField");
    }
    
    @Override
    public String getStyle() {
        return "background: black; color: white; border: 3px solid white";
    }
}

class HighContrastCard implements Card {
    @Override
    public void render() {
        System.out.println("🗃️ Rendering High Contrast Card");
    }
    
    @Override
    public String getStyle() {
        return "background: black; color: white; border: 3px solid white";
    }
}

class HighContrastPanel implements Panel {
    @Override
    public void render() {
        System.out.println("📄 Rendering High Contrast Panel");
    }
    
    @Override
    public String getStyle() {
        return "background: black; color: white; padding: 16px; border: 3px solid white";
    }
}

// Abstract Factory
interface UIFactory {
    Button createButton();
    TextField createTextField();
    Card createCard();
    Panel createPanel();
    String getThemeName();
}

// Concrete Factories
class LightThemeFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new LightButton();
    }
    
    @Override
    public TextField createTextField() {
        return new LightTextField();
    }
    
    @Override
    public Card createCard() {
        return new LightCard();
    }
    
    @Override
    public Panel createPanel() {
        return new LightPanel();
    }
    
    @Override
    public String getThemeName() {
        return "Light Theme";
    }
}

class DarkThemeFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new DarkButton();
    }
    
    @Override
    public TextField createTextField() {
        return new DarkTextField();
    }
    
    @Override
    public Card createCard() {
        return new DarkCard();
    }
    
    @Override
    public Panel createPanel() {
        return new DarkPanel();
    }
    
    @Override
    public String getThemeName() {
        return "Dark Theme";
    }
}

class HighContrastThemeFactory implements UIFactory {
    @Override
    public Button createButton() {
        return new HighContrastButton();
    }
    
    @Override
    public TextField createTextField() {
        return new HighContrastTextField();
    }
    
    @Override
    public Card createCard() {
        return new HighContrastCard();
    }
    
    @Override
    public Panel createPanel() {
        return new HighContrastPanel();
    }
    
    @Override
    public String getThemeName() {
        return "High Contrast Theme";
    }
}

// Theme Manager (Singleton)
class ThemeManager {
    private static ThemeManager instance;
    private UIFactory currentFactory;
    
    private ThemeManager() {
        this.currentFactory = new LightThemeFactory(); // Default theme
    }
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void setTheme(UIFactory factory) {
        this.currentFactory = factory;
        System.out.println("🎨 Theme switched to: " + factory.getThemeName());
    }
    
    public UIFactory getCurrentFactory() {
        return currentFactory;
    }
}

// Application using the UI components
class Application {
    private final Button submitButton;
    private final TextField nameField;
    private final Card profileCard;
    private final Panel mainPanel;
    
    public Application(UIFactory factory) {
        // Create all components from the same factory
        this.submitButton = factory.createButton();
        this.nameField = factory.createTextField();
        this.profileCard = factory.createCard();
        this.mainPanel = factory.createPanel();
    }
    
    public void render() {
        System.out.println("\n--- Rendering Application ---");
        mainPanel.render();
        System.out.println("  Style: " + mainPanel.getStyle());
        
        profileCard.render();
        System.out.println("  Style: " + profileCard.getStyle());
        
        nameField.render();
        System.out.println("  Style: " + nameField.getStyle());
        
        submitButton.render();
        System.out.println("  Style: " + submitButton.getStyle());
        System.out.println("--- Rendering Complete ---\n");
    }
}

// Demo
public class UIThemeDemo {
    public static void main(String[] args) throws InterruptedException {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        System.out.println("=== UI Theme System Demo ===\n");
        
        // Light Theme
        System.out.println("☀️ Using Light Theme");
        themeManager.setTheme(new LightThemeFactory());
        Application app1 = new Application(themeManager.getCurrentFactory());
        app1.render();
        
        Thread.sleep(1000);
        
        // Dark Theme
        System.out.println("🌙 Switching to Dark Theme");
        themeManager.setTheme(new DarkThemeFactory());
        Application app2 = new Application(themeManager.getCurrentFactory());
        app2.render();
        
        Thread.sleep(1000);
        
        // High Contrast Theme
        System.out.println("👁️ Switching to High Contrast Theme");
        themeManager.setTheme(new HighContrastThemeFactory());
        Application app3 = new Application(themeManager.getCurrentFactory());
        app3.render();
    }
}
```

### Key Points to Mention
- **Abstract Factory**: Creates families of related objects
- **Consistency**: All components share same theme
- **Theme Isolation**: Theme logic encapsulated in factories
- **Extensibility**: New themes added without modifying existing code
- **Runtime Switching**: Can change themes dynamically

---

## 21. Builder Pattern - SQL Query Builder

### Problem Statement
Design a SQL query builder that:
- Build SELECT, INSERT, UPDATE, DELETE queries
- Support WHERE conditions, JOINs, ORDER BY, GROUP BY
- Method chaining for fluent API
- Query validation
- Parameterized queries (SQL injection prevention)

### Interview Checklist

**Requirements:**
- [ ] SQL operations supported
- [ ] Complex joins needed?
- [ ] Query validation?
- [ ] Multiple database support?
- [ ] ORM-like features?

**Design Decisions:**
- [ ] Builder pattern for query construction
- [ ] Method chaining for fluency
- [ ] Immutable or mutable builder?
- [ ] Validation strategy

**Implementation Focus:**
- [ ] Clean, readable API
- [ ] SQL injection prevention
- [ ] Query validation
- [ ] Error handling

### Solution

```java
// Query representation
class SQLQuery {
    private final String sql;
    private final List<Object> parameters;
    
    public SQLQuery(String sql, List<Object> parameters) {
        this.sql = sql;
        this.parameters = new ArrayList<>(parameters);
    }
    
    public String getSql() {
        return sql;
    }
    
    public List<Object> getParameters() {
        return new ArrayList<>(parameters);
    }
    
    @Override
    public String toString() {
        return "SQL: " + sql + "\nParameters: " + parameters;
    }
}

// Builder for SELECT queries
class SelectQueryBuilder {
    private final List<String> columns;
    private String tableName;
    private final List<String> joins;
    private final List<String> whereConditions;
    private final List<Object> parameters;
    private final List<String> groupByColumns;
    private String havingCondition;
    private final List<String> orderByColumns;
    private Integer limit;
    private Integer offset;
    
    public SelectQueryBuilder() {
        this.columns = new ArrayList<>();
        this.joins = new ArrayList<>();
        this.whereConditions = new ArrayList<>();
        this.parameters = new ArrayList<>();
        this.groupByColumns = new ArrayList<>();
        this.orderByColumns = new ArrayList<>();
    }
    
    public SelectQueryBuilder select(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }
    
    public SelectQueryBuilder from(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public SelectQueryBuilder join(String tableName, String condition) {
        joins.add("INNER JOIN " + tableName + " ON " + condition);
        return this;
    }
    
    public SelectQueryBuilder leftJoin(String tableName, String condition) {
        joins.add("LEFT JOIN " + tableName + " ON " + condition);
        return this;
    }
    
    public SelectQueryBuilder where(String condition, Object... params) {
        whereConditions.add(condition);
        parameters.addAll(Arrays.asList(params));
        return this;
    }
    
    public SelectQueryBuilder and(String condition, Object... params) {
        if (!whereConditions.isEmpty()) {
            whereConditions.add("AND " + condition);
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }
    
    public SelectQueryBuilder or(String condition, Object... params) {
        if (!whereConditions.isEmpty()) {
            whereConditions.add("OR " + condition);
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }
    
    public SelectQueryBuilder groupBy(String... columns) {
        this.groupByColumns.addAll(Arrays.asList(columns));
        return this;
    }
    
    public SelectQueryBuilder having(String condition) {
        this.havingCondition = condition;
        return this;
    }
    
    public SelectQueryBuilder orderBy(String... columns) {
        this.orderByColumns.addAll(Arrays.asList(columns));
        return this;
    }
    
    public SelectQueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }
    
    public SelectQueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }
    
    public SQLQuery build() {
        validate();
        
        StringBuilder sql = new StringBuilder("SELECT ");
        
        // Columns
        if (columns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }
        
        // FROM
        sql.append(" FROM ").append(tableName);
        
        // JOINs
        if (!joins.isEmpty()) {
            sql.append(" ");
            sql.append(String.join(" ", joins));
        }
        
        // WHERE
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" ", whereConditions));
        }
        
        // GROUP BY
        if (!groupByColumns.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(String.join(", ", groupByColumns));
        }
        
        // HAVING
        if (havingCondition != null) {
            sql.append(" HAVING ").append(havingCondition);
        }
        
        // ORDER BY
        if (!orderByColumns.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(String.join(", ", orderByColumns));
        }
        
        // LIMIT
        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }
        
        // OFFSET
        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }
        
        return new SQLQuery(sql.toString(), parameters);
    }
    
    private void validate() {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalStateException("Table name is required");
        }
    }
}

// Builder for INSERT queries
class InsertQueryBuilder {
    private String tableName;
    private final List<String> columns;
    private final List<Object> values;
    
    public InsertQueryBuilder() {
        this.columns = new ArrayList<>();
        this.values = new ArrayList<>();
    }
    
    public InsertQueryBuilder into(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public InsertQueryBuilder columns(String... columns) {
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }
    
    public InsertQueryBuilder values(Object... values) {
        this.values.addAll(Arrays.asList(values));
        return this;
    }
    
    public SQLQuery build() {
        validate();
        
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName);
        
        // Columns
        sql.append(" (");
        sql.append(String.join(", ", columns));
        sql.append(")");
        
        // Values
        sql.append(" VALUES (");
        sql.append(String.join(", ", Collections.nCopies(values.size(), "?")));
        sql.append(")");
        
        return new SQLQuery(sql.toString(), values);
    }
    
    private void validate() {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalStateException("Table name is required");
        }
        if (columns.isEmpty()) {
            throw new IllegalStateException("Columns are required");
        }
        if (columns.size() != values.size()) {
            throw new IllegalStateException("Columns and values count mismatch");
        }
    }
}

// Builder for UPDATE queries
class UpdateQueryBuilder {
    private String tableName;
    private final Map<String, Object> setValues;
    private final List<String> whereConditions;
    private final List<Object> whereParameters;
    
    public UpdateQueryBuilder() {
        this.setValues = new LinkedHashMap<>();
        this.whereConditions = new ArrayList<>();
        this.whereParameters = new ArrayList<>();
    }
    
    public UpdateQueryBuilder table(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public UpdateQueryBuilder set(String column, Object value) {
        setValues.put(column, value);
        return this;
    }
    
    public UpdateQueryBuilder where(String condition, Object... params) {
        whereConditions.add(condition);
        whereParameters.addAll(Arrays.asList(params));
        return this;
    }
    
    public UpdateQueryBuilder and(String condition, Object... params) {
        if (!whereConditions.isEmpty()) {
            whereConditions.add("AND " + condition);
            whereParameters.addAll(Arrays.asList(params));
        }
        return this;
    }
    
    public SQLQuery build() {
        validate();
        
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        
        // SET clause
        List<String> setClauses = new ArrayList<>();
        List<Object> allParams = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : setValues.entrySet()) {
            setClauses.add(entry.getKey() + " = ?");
            allParams.add(entry.getValue());
        }
        sql.append(String.join(", ", setClauses));
        
        // WHERE clause
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" ", whereConditions));
            allParams.addAll(whereParameters);
        }
        
        return new SQLQuery(sql.toString(), allParams);
    }
    
    private void validate() {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalStateException("Table name is required");
        }
        if (setValues.isEmpty()) {
            throw new IllegalStateException("SET values are required");
        }
    }
}

// Builder for DELETE queries
class DeleteQueryBuilder {
    private String tableName;
    private final List<String> whereConditions;
    private final List<Object> parameters;
    
    public DeleteQueryBuilder() {
        this.whereConditions = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }
    
    public DeleteQueryBuilder from(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public DeleteQueryBuilder where(String condition, Object... params) {
        whereConditions.add(condition);
        parameters.addAll(Arrays.asList(params));
        return this;
    }
    
    public DeleteQueryBuilder and(String condition, Object... params) {
        if (!whereConditions.isEmpty()) {
            whereConditions.add("AND " + condition);
            parameters.addAll(Arrays.asList(params));
        }
        return this;
    }
    
    public SQLQuery build() {
        validate();
        
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(tableName);
        
        // WHERE clause
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" ", whereConditions));
        }
        
        return new SQLQuery(sql.toString(), parameters);
    }
    
    private void validate() {
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalStateException("Table name is required");
        }
    }
}

// Demo
public class SQLQueryBuilderDemo {
    public static void main(String[] args) {
        System.out.println("=== SQL Query Builder Demo ===\n");
        
        // SELECT query
        System.out.println("--- SELECT Query ---");
        SQLQuery selectQuery = new SelectQueryBuilder()
            .select("u.id", "u.name", "u.email", "COUNT(o.id) as order_count")
            .from("users u")
            .leftJoin("orders o", "u.id = o.user_id")
            .where("u.status = ?", "active")
            .and("u.created_at > ?", "2024-01-01")
            .groupBy("u.id", "u.name", "u.email")
            .having("COUNT(o.id) > 5")
            .orderBy("u.name ASC")
            .limit(10)
            .offset(0)
            .build();
        
        System.out.println(selectQuery);
        System.out.println();
        
        // INSERT query
        System.out.println("--- INSERT Query ---");
        SQLQuery insertQuery = new InsertQueryBuilder()
            .into("users")
            .columns("name", "email", "status")
            .values("John Doe", "john@example.com", "active")
            .build();
        
        System.out.println(insertQuery);
        System.out.println();
        
        // UPDATE query
        System.out.println("--- UPDATE Query ---");
        SQLQuery updateQuery = new UpdateQueryBuilder()
            .table("users")
            .set("status", "inactive")
            .set("updated_at", "2024-01-14")
            .where("id = ?", 123)
            .and("status = ?", "active")
            .build();
        
        System.out.println(updateQuery);
        System.out.println();
        
        // DELETE query
        System.out.println("--- DELETE Query ---");
        SQLQuery deleteQuery = new DeleteQueryBuilder()
            .from("users")
            .where("status = ?", "deleted")
            .and("created_at < ?", "2020-01-01")
            .build();
        
        System.out.println(deleteQuery);
        System.out.println();
        
        // Complex SELECT with multiple joins
        System.out.println("--- Complex SELECT Query ---");
        SQLQuery complexQuery = new SelectQueryBuilder()
            .select("p.name", "c.name as category", "SUM(oi.quantity) as total_sold")
            .from("products p")
            .join("categories c", "p.category_id = c.id")
            .join("order_items oi", "p.id = oi.product_id")
            .join("orders o", "oi.order_id = o.id")
            .where("o.status = ?", "completed")
            .and("o.created_at BETWEEN ? AND ?", "2024-01-01", "2024-12-31")
            .groupBy("p.id", "p.name", "c.name")
            .orderBy("total_sold DESC")
            .limit(20)
            .build();
        
        System.out.println(complexQuery);
    }
}
```

### Key Points to Mention
- **Builder Pattern**: Step-by-step query construction
- **Fluent API**: Method chaining for readability
- **SQL Injection Prevention**: Parameterized queries
- **Validation**: Query validation before execution
- **Separation**: Different builders for different query types

---

## 22. Prototype Pattern - Game Object Cloning

### Problem Statement
Design a game object system that:
- Clone game entities efficiently (enemies, weapons, items)
- Deep copy of complex objects
- Registry of prototypes
- Customization after cloning
- Performance optimization

### Interview Checklist

**Requirements:**
- [ ] Object types to clone
- [ ] Deep vs shallow copy needed?
- [ ] Prototype registry?
- [ ] Serialization support?
- [ ] Performance constraints?

**Design Decisions:**
- [ ] Prototype pattern for cloning
- [ ] Registry pattern for prototypes
- [ ] Clone method implementation
- [ ] Deep copy strategy

**Implementation Focus:**
- [ ] Proper cloning logic
- [ ] Handle nested objects
- [ ] Performance considerations
- [ ] Registry management

### Solution

```java
// Prototype interface
interface GameEntity extends Cloneable {
    GameEntity clone();
    void display();
    String getName();
}

// Weapon class (nested object)
class Weapon implements Cloneable {
    private String name;
    private int damage;
    private String type;
    
    public Weapon(String name, int damage, String type) {
        this.name = name;
        this.damage = damage;
        this.type = type;
    }
    
    @Override
    protected Weapon clone() {
        try {
            return (Weapon) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Weapon(this.name, this.damage, this.type);
        }
    }
    
    public String getName() { return name; }
    public int getDamage() { return damage; }
    public String getType() { return type; }
    
    public void setDamage(int damage) { this.damage = damage; }
    
    @Override
    public String toString() {
        return name + " (" + type + ", Damage: " + damage + ")";
    }
}

// Equipment class
class Equipment implements Cloneable {
    private String armor;
    private int defense;
    private List<String> accessories;
    
    public Equipment(String armor, int defense) {
        this.armor = armor;
        this.defense = defense;
        this.accessories = new ArrayList<>();
    }
    
    public void addAccessory(String accessory) {
        accessories.add(accessory);
    }
    
    @Override
    protected Equipment clone() {
        try {
            Equipment cloned = (Equipment) super.clone();
            // Deep copy of mutable list
            cloned.accessories = new ArrayList<>(this.accessories);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    public String getArmor() { return armor; }
    public int getDefense() { return defense; }
    public List<String> getAccessories() { return new ArrayList<>(accessories); }
    
    @Override
    public String toString() {
        return armor + " (Defense: " + defense + ", Accessories: " + accessories + ")";
    }
}

// Concrete Prototype: Enemy
class Enemy implements GameEntity {
    private String name;
    private int health;
    private int attackPower;
    private String type;
    private Weapon weapon;
    private Equipment equipment;
    private Map<String, Integer> attributes;
    
    public Enemy(String name, int health, int attackPower, String type) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.type = type;
        this.attributes = new HashMap<>();
    }
    
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
    
    public void setAttribute(String key, int value) {
        attributes.put(key, value);
    }
    
    @Override
    public Enemy clone() {
        try {
            Enemy cloned = (Enemy) super.clone();
            
            // Deep copy of nested objects
            if (this.weapon != null) {
                cloned.weapon = this.weapon.clone();
            }
            if (this.equipment != null) {
                cloned.equipment = this.equipment.clone();
            }
            
            // Deep copy of mutable map
            cloned.attributes = new HashMap<>(this.attributes);
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    @Override
    public void display() {
        System.out.println("👹 Enemy: " + name);
        System.out.println("   Type: " + type);
        System.out.println("   Health: " + health);
        System.out.println("   Attack: " + attackPower);
        if (weapon != null) {
            System.out.println("   Weapon: " + weapon);
        }
        if (equipment != null) {
            System.out.println("   Equipment: " + equipment);
        }
        if (!attributes.isEmpty()) {
            System.out.println("   Attributes: " + attributes);
        }
    }
    
    @Override
    public String getName() { return name; }
    
    public void setName(String name) { this.name = name; }
    public void setHealth(int health) { this.health = health; }
    public void setAttackPower(int attackPower) { this.attackPower = attackPower; }
}

// Concrete Prototype: Player
class Player implements GameEntity {
    private String name;
    private String characterClass;
    private int level;
    private int health;
    private int mana;
    private Weapon weapon;
    private Equipment equipment;
    private List<String> skills;
    
    public Player(String name, String characterClass, int level) {
        this.name = name;
        this.characterClass = characterClass;
        this.level = level;
        this.health = 100 * level;
        this.mana = 50 * level;
        this.skills = new ArrayList<>();
    }
    
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
    
    public void addSkill(String skill) {
        skills.add(skill);
    }
    
    @Override
    public Player clone() {
        try {
            Player cloned = (Player) super.clone();
            
            // Deep copy
            if (this.weapon != null) {
                cloned.weapon = this.weapon.clone();
            }
            if (this.equipment != null) {
                cloned.equipment = this.equipment.clone();
            }
            cloned.skills = new ArrayList<>(this.skills);
            
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
    
    @Override
    public void display() {
        System.out.println("🧙 Player: " + name);
        System.out.println("   Class: " + characterClass);
        System.out.println("   Level: " + level);
        System.out.println("   Health: " + health + ", Mana: " + mana);
        if (weapon != null) {
            System.out.println("   Weapon: " + weapon);
        }
        if (equipment != null) {
            System.out.println("   Equipment: " + equipment);
        }
        if (!skills.isEmpty()) {
            System.out.println("   Skills: " + skills);
        }
    }
    
    @Override
    public String getName() { return name; }
    
    public void setName(String name) { this.name = name; }
    public void setLevel(int level) { 
        this.level = level; 
        this.health = 100 * level;
        this.mana = 50 * level;
    }
}

// Prototype Registry
class GameEntityRegistry {
    private final Map<String, GameEntity> prototypes;
    
    public GameEntityRegistry() {
        this.prototypes = new ConcurrentHashMap<>();
    }
    
    public void registerPrototype(String key, GameEntity prototype) {
        prototypes.put(key, prototype);
        System.out.println("✓ Registered prototype: " + key);
    }
    
    public GameEntity getPrototype(String key) {
        GameEntity prototype = prototypes.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + key);
        }
        return prototype.clone();
    }
    
    public void listPrototypes() {
        System.out.println("\n📋 Available Prototypes:");
        prototypes.keySet().forEach(key -> 
            System.out.println("  - " + key + ": " + prototypes.get(key).getName())
        );
        System.out.println();
    }
}

// Demo
public class GamePrototypeDemo {
    public static void main(String[] args) {
        System.out.println("=== Game Prototype System Demo ===\n");
        
        GameEntityRegistry registry = new GameEntityRegistry();
        
        // Create prototype enemies
        System.out.println("--- Creating Prototypes ---");
        
        // Orc prototype
        Enemy orcPrototype = new Enemy("Orc Warrior", 150, 25, "Melee");
        orcPrototype.setWeapon(new Weapon("Battle Axe", 30, "Melee"));
        Equipment orcEquipment = new Equipment("Leather Armor", 15);
        orcEquipment.addAccessory("Shield");
        orcPrototype.setEquipment(orcEquipment);
        orcPrototype.setAttribute("rage", 50);
        registry.registerPrototype("orc", orcPrototype);
        
        // Dragon prototype
        Enemy dragonPrototype = new Enemy("Fire Dragon", 500, 75, "Flying");
        dragonPrototype.setWeapon(new Weapon("Fire Breath", 100, "Magic"));
        Equipment dragonEquipment = new Equipment("Dragon Scales", 50);
        dragonEquipment.addAccessory("Wings");
        dragonPrototype.setEquipment(dragonEquipment);
        dragonPrototype.setAttribute("flight_speed", 100);
        registry.registerPrototype("dragon", dragonPrototype);
        
        // Player prototype
        Player magePrototype = new Player("Mage", "Wizard", 1);
        magePrototype.setWeapon(new Weapon("Staff of Power", 20, "Magic"));
        Equipment mageEquipment = new Equipment("Robe", 5);
        mageEquipment.addAccessory("Magic Ring");
        magePrototype.setEquipment(mageEquipment);
        magePrototype.addSkill("Fireball");
        magePrototype.addSkill("Ice Shield");
        registry.registerPrototype("mage", magePrototype);
        
        registry.listPrototypes();
        
        // Clone and customize
        System.out.println("--- Cloning Entities ---\n");
        
        // Clone orcs
        Enemy orc1 = (Enemy) registry.getPrototype("orc");
        orc1.setName("Orc Grunt #1");
        
        Enemy orc2 = (Enemy) registry.getPrototype("orc");
        orc2.setName("Orc Grunt #2");
        orc2.setHealth(200); // Stronger variant
        orc2.setAttackPower(35);
        
        // Clone dragon
        Enemy boss = (Enemy) registry.getPrototype("dragon");
        boss.setName("Ancient Fire Dragon");
        boss.setHealth(1000); // Boss-level health
        
        // Clone players
        Player player1 = (Player) registry.getPrototype("mage");
        player1.setName("Gandalf");
        player1.setLevel(5);
        player1.addSkill("Teleport");
        
        Player player2 = (Player) registry.getPrototype("mage");
        player2.setName("Merlin");
        player2.setLevel(3);
        
        // Display cloned entities
        System.out.println("--- Cloned Entities ---\n");
        orc1.display();
        System.out.println();
        
        orc2.display();
        System.out.println();
        
        boss.display();
        System.out.println();
        
        player1.display();
        System.out.println();
        
        player2.display();
        System.out.println();
        
        // Verify deep copy
        System.out.println("--- Verifying Deep Copy ---");
        System.out.println("Original prototype unchanged:");
        System.out.println("Orc prototype name: " + orcPrototype.getName());
        System.out.println("Cloned orc1 name: " + orc1.getName());
        System.out.println("Cloned orc2 name: " + orc2.getName());
    }
}
```

### Key Points to Mention
- **Prototype Pattern**: Create objects by cloning existing ones
- **Deep Copy**: Proper cloning of nested objects
- **Performance**: Faster than creating objects from scratch
- **Registry**: Central location for managing prototypes
- **Flexibility**: Easy to create variants by cloning and customizing

---

## Summary

This part covered **6 essential design patterns**:

1. **Adapter Pattern** - Integrates incompatible payment gateways with unified interface
2. **Facade Pattern** - Simplifies smart home automation with preset modes
3. **Factory Pattern** - Creates notification handlers for multiple channels
4. **Abstract Factory** - Generates complete UI theme families
5. **Builder Pattern** - Constructs complex SQL queries with fluent API
6. **Prototype Pattern** - Clones game entities efficiently

Each pattern includes:
✅ Production-ready implementation  
✅ Interview checklist  
✅ Design decisions rationale  
✅ Complete working demos  
✅ Key interview talking points
