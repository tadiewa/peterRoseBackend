# Yoco Payment Gateway Integration Guide

## 🎯 Overview

Complete Yoco payment integration for Peter Rose flower shop following SOLID principles.

---

## 📦 Files Created

### Backend (Spring Boot)

```
/model/
├── Payment.java              ✅ Payment entity
├── PaymentStatus.java        ✅ Enum: pending, processing, succeeded, failed, cancelled, refunded
└── PaymentMethod.java        ✅ Enum: card, eft, snapscan, cash

/dto/request/
└── PaymentCreateDTO.java     ✅ For creating payments

/dto/response/
└── PaymentResponseDTO.java   ✅ Payment response with redirect URL

/service/
├── PaymentService.java       ✅ Interface (Dependency Inversion)
└── /impl/
    └── PaymentServiceImpl.java ✅ Yoco integration logic

/repository/
└── PaymentRepository.java    ✅ JPA repository

/controller/
└── PaymentController.java    ✅ REST endpoints

/config/
└── YocoConfig.java          ✅ Configuration properties
```

---

## 🔧 Setup Steps

### 1. Get Yoco API Keys

1. Sign up at https://www.yoco.com/za/
2. Go to Developer Dashboard
3. Get your keys:
   - **Secret Key** (for backend) - Keep this secure!
   - **Public Key** (for frontend) - Safe to expose

---

### 2. Add Yoco Configuration

**application.properties:**

```properties
# Yoco Payment Gateway
yoco.secret-key=${YOCO_SECRET_KEY:sk_test_xxxxxxxxxxxxxxxx}
yoco.public-key=${YOCO_PUBLIC_KEY:pk_test_xxxxxxxxxxxxxxxx}
yoco.api-url=https://payments.yoco.com
yoco.webhook-url=${APP_URL}/api/payments/webhook
yoco.success-url=${FRONTEND_URL}/checkout/success
yoco.cancel-url=${FRONTEND_URL}/checkout/cancel

# For production
# yoco.api-url=https://payments.yoco.com
# For test/development
# yoco.api-url=https://payments.yoco.com
```

**Environment Variables (Recommended):**

```bash
# .env or system environment
export YOCO_SECRET_KEY=sk_test_xxxxxxxxxxxxxxxx
export YOCO_PUBLIC_KEY=pk_test_xxxxxxxxxxxxxxxx
export APP_URL=http://localhost:8080
export FRONTEND_URL=http://localhost:3000
```

---

### 3. Add Dependencies

**pom.xml:**

```xml
<!-- Already have these from Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- For logging -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

---

### 4. Database Migration

The `payments` table will be auto-created by JPA. Schema:

```sql
CREATE TABLE payments (
    id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    yoco_payment_id VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'ZAR',
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    paid_at TIMESTAMP,
    failure_reason VARCHAR(500)
);
```

---

## 🔌 API Endpoints

### Create Payment

```bash
POST /api/payments
Content-Type: application/json

{
  "orderId": "ORD-2026-001",
  "amount": 1350.0,
  "currency": "ZAR",
  "successUrl": "http://localhost:3000/checkout/success",
  "cancelUrl": "http://localhost:3000/checkout/cancel",
  "customerEmail": "john@example.com",
  "customerName": "John Doe"
}
```

**Response:**
```json
{
  "id": "pay-uuid-123",
  "orderId": "ORD-2026-001",
  "amount": 1350.0,
  "currency": "ZAR",
  "status": "pending",
  "redirectUrl": "https://checkout.yoco.com/xxxxxxxx",
  "createdAt": "2026-03-16T10:00:00"
}
```

### Get Payment by ID

```bash
GET /api/payments/{paymentId}
```

### Get Payment by Order ID

```bash
GET /api/payments/order/{orderId}
```

### Verify Payment

```bash
GET /api/payments/{paymentId}/verify
```

### Webhook (Yoco calls this)

```bash
POST /api/payments/webhook
X-Yoco-Signature: sha256=xxxxx
Content-Type: application/json

{
  "type": "payment.succeeded",
  "payload": {...}
}
```

---

## 🎨 Frontend Integration

### 1. Update Checkout Flow

**Updated Checkout Page Flow:**

```typescript
// After user clicks "Place Order"
async function handlePlaceOrder() {
  try {
    // 1. Create order
    const order = await createOrder(orderDTO);
    
    // 2. Create payment
    const payment = await createPayment({
      orderId: order.id,
      amount: order.total,
      currency: "ZAR",
      successUrl: `${window.location.origin}/checkout/success`,
      cancelUrl: `${window.location.origin}/checkout/cancel`,
      customerEmail: customerInfo.email,
      customerName: `${customerInfo.firstName} ${customerInfo.lastName}`
    });
    
    // 3. Redirect to Yoco checkout
    window.location.href = payment.redirectUrl;
    
  } catch (error) {
    toast.error("Payment failed");
  }
}
```

### 2. Create Payment API Function

**lib/api.ts:**

```typescript
export async function createPayment(paymentDTO: {
  orderId: string;
  amount: number;
  currency: string;
  successUrl: string;
  cancelUrl: string;
  customerEmail?: string;
  customerName?: string;
}): Promise<PaymentResponseDTO> {
  const res = await fetch(`${API_URL}/api/payments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(paymentDTO),
  });
  
  if (!res.ok) {
    const error = await res.json();
    throw new Error(error.message || 'Failed to create payment');
  }
  
  return res.json();
}

export async function getPaymentByOrderId(orderId: string): Promise<PaymentResponseDTO> {
  const res = await fetch(`${API_URL}/api/payments/order/${orderId}`);
  if (!res.ok) throw new Error('Failed to fetch payment');
  return res.json();
}
```

### 3. Create Success Page

**app/checkout/success/page.tsx:**

```typescript
"use client";

import { useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { CheckCircle2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { getPaymentByOrderId } from "@/lib/api";

export default function PaymentSuccessPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const orderId = searchParams.get("orderId");
    if (orderId) {
      getPaymentByOrderId(orderId)
        .then(setPayment)
        .finally(() => setLoading(false));
    }
  }, [searchParams]);

  if (loading) return <div>Loading...</div>;

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center max-w-md">
        <CheckCircle2 className="h-20 w-20 text-green-600 mx-auto mb-6" />
        <h1 className="text-3xl font-bold mb-4">Payment Successful!</h1>
        <p className="text-muted-foreground mb-8">
          Your order has been confirmed and payment received.
        </p>
        <div className="space-y-3">
          <Button onClick={() => router.push(`/track?id=${payment?.orderId}`)}>
            Track Order
          </Button>
          <Button variant="outline" onClick={() => router.push("/shop")}>
            Continue Shopping
          </Button>
        </div>
      </div>
    </div>
  );
}
```

### 4. Create Cancel Page

**app/checkout/cancel/page.tsx:**

```typescript
"use client";

import { XCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

export default function PaymentCancelPage() {
  const router = useRouter();

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center max-w-md">
        <XCircle className="h-20 w-20 text-red-600 mx-auto mb-6" />
        <h1 className="text-3xl font-bold mb-4">Payment Cancelled</h1>
        <p className="text-muted-foreground mb-8">
          Your payment was cancelled. Your order is still in your cart.
        </p>
        <Button onClick={() => router.push("/checkout")}>
          Try Again
        </Button>
      </div>
    </div>
  );
}
```

---

## 🔄 Complete Payment Flow

```
1. Customer fills checkout form
    ↓
2. Click "Place Order"
    ↓
3. Frontend: POST /api/orders (create order)
    ↓
4. Order created with status "PENDING"
    ↓
5. Frontend: POST /api/payments (create payment)
    ↓
6. Backend: Calls Yoco API to create checkout
    ↓
7. Payment record created with status "PENDING"
    ↓
8. Backend returns: { redirectUrl: "https://checkout.yoco.com/xxx" }
    ↓
9. Frontend: Redirect user to Yoco checkout
    ↓
10. User enters card details on Yoco
    ↓
11. Yoco processes payment
    ↓
12. Success → Redirect to /checkout/success?orderId=xxx
    ↓
13. Yoco sends webhook to /api/payments/webhook
    ↓
14. Backend: Update payment status to "SUCCEEDED"
    ↓
15. Backend: Update order status to "CONFIRMED"
    ↓
16. Customer sees success page with order details
```

---

## 🧪 Testing

### Test Mode

Yoco provides test cards:

**Successful Payment:**
- Card: `4242 4242 4242 4242`
- Expiry: Any future date
- CVV: Any 3 digits

**Failed Payment:**
- Card: `4000 0000 0000 0002`

### Test Endpoints

```bash
# Create test payment
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-2026-001",
    "amount": 1350.0,
    "currency": "ZAR",
    "successUrl": "http://localhost:3000/checkout/success",
    "cancelUrl": "http://localhost:3000/checkout/cancel",
    "customerEmail": "test@example.com"
  }'

# Get payment
curl http://localhost:8080/api/payments/order/ORD-2026-001
```

---

## 🔒 Security Best Practices

✅ **Never expose secret key** - Keep in environment variables
✅ **Verify webhook signatures** - Implement HMAC verification
✅ **Use HTTPS in production** - Required by Yoco
✅ **Validate amounts** - Verify order total matches payment
✅ **Idempotency** - Don't process same payment twice
✅ **Log everything** - For debugging and fraud detection

---

## 📝 Webhook Implementation (TODO)

You need to implement webhook signature verification:

```java
private boolean verifyWebhookSignature(String payload, String signature) {
    try {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
            yocoConfig.getSecretKey().getBytes(), 
            "HmacSHA256"
        );
        mac.init(secretKey);
        
        byte[] hash = mac.doFinal(payload.getBytes());
        String calculated = Base64.getEncoder().encodeToString(hash);
        
        return calculated.equals(signature);
    } catch (Exception e) {
        return false;
    }
}
```

---

## 🚀 Go Live Checklist

- [ ] Switch to production Yoco keys
- [ ] Update URLs to production domain
- [ ] Enable HTTPS
- [ ] Implement webhook signature verification
- [ ] Add error handling and retry logic
- [ ] Set up monitoring and alerts
- [ ] Test with real small amount
- [ ] Update terms and conditions

---

Your Yoco payment integration is ready! 🎉
