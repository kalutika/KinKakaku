# BTMC API Field Reference

## 📋 Understanding BTMC API Data Fields

This document explains the meaning of each field returned by the BTMC Gold Price API.

---

## 🏷️ Field Naming Convention

BTMC API uses XML attributes with a special format: `@field_variant`

- **Prefix**: `@` indicates an XML attribute
- **Field**: 2-3 letter code indicating data type
- **Variant**: `_1`, `_2`, `_3`, `_4` for multiple price categories

---

## 📊 Available Fields

### 🏷️ Name Fields (Product Name)
| Field | Description | Example |
|-------|-------------|---------|
| `@n_1` | Product name variant 1 | "Vàng SJC 1 lượng" |
| `@n_2` | Product name variant 2 | "Vàng SJC 5 chỉ" |
| `@n_3` | Product name variant 3 | "Vàng nhẫn 9999" |
| `@n_4` | Product name variant 4 | "Bạc SJC" |

**Purpose**: Different gold/silver product types or brands

---

### 🔖 Category Fields (Type/Category)
| Field | Description | Example |
|-------|-------------|---------|
| `@k_1` | Category/Type variant 1 | "Vàng miếng" |
| `@k_2` | Category/Type variant 2 | "Vàng nhẫn" |
| `@k_3` | Category/Type variant 3 | "Bạc" |
| `@k_4` | Category/Type variant 4 | Other types |

**Purpose**: Classification of precious metal products

---

### 💰 Buy Price Fields (Giá Mua)
| Field | Description | Example |
|-------|-------------|---------|
| `@pb_1` | Buy price variant 1 (VND) | "75500000" |
| `@pb_2` | Buy price variant 2 (VND) | "75400000" |
| `@pb_3` | Buy price variant 3 (VND) | "5500000" |
| `@pb_4` | Buy price variant 4 (VND) | "850000" |

**Purpose**: Purchase price from the dealer (what they pay to buy from you)
**Note**: Prices are in Vietnamese Dong (VND)

---

### 💵 Sell Price Fields (Giá Bán)
| Field | Description | Example |
|-------|-------------|---------|
| `@ps_1` | Sell price variant 1 (VND) | "76500000" |
| `@ps_2` | Sell price variant 2 (VND) | "76400000" |
| `@ps_3` | Sell price variant 3 (VND) | "5700000" |
| `@ps_4` | Sell price variant 4 (VND) | "900000" |

**Purpose**: Sale price from the dealer (what you pay to buy from them)
**Note**: Prices are in Vietnamese Dong (VND)

---

### ⚖️ Weight/Purity Fields (Trọng Lượng/Độ Tinh Khiết)
| Field | Description | Example |
|-------|-------------|---------|
| `@pt_1` | Weight/purity variant 1 | "1 lượng" |
| `@pt_2` | Weight/purity variant 2 | "5 chỉ" |
| `@pt_3` | Weight/purity variant 3 | "9999" (purity) |
| `@pt_4` | Weight/purity variant 4 | "10 gram" |

**Purpose**: Weight or purity information
**Common Units**:
- **Lượng** (tael) - Traditional Vietnamese gold unit (~37.5g)
- **Chỉ** (mace) - 1/10 of a lượng (~3.75g)
- **Gram** - Standard metric unit
- **9999** - Purity level (99.99% pure gold)

---

### 📅 Date Fields (Ngày Cập Nhật)
| Field | Description | Example |
|-------|-------------|---------|
| `@d_1` | Last update date variant 1 | "24/04/2026" |
| `@d_2` | Last update date variant 2 | "24/04/2026 10:30" |
| `@d_3` | Last update date variant 3 | "24-04-2026" |
| `@d_4` | Last update date variant 4 | ISO format |

**Purpose**: Timestamp of last price update
**Format**: Typically DD/MM/YYYY

---

### 🔢 Row Identifier
| Field | Description | Example |
|-------|-------------|---------|
| `@row` | Row number/identifier | "1", "2", "3"... |

**Purpose**: Unique identifier for each data row

---

## 🎯 Usage in Your App

### Current Mapping Logic

```kotlin
fun getName(): String {
    return `@n_1` ?: `@n_2` ?: `@n_3` ?: `@n_4` ?: "Unknown Product"
}
```
**Strategy**: Uses first available name field, falls back to next if null

```kotlin
fun getBuyPrice(): Double? {
    return (`@pb_1` ?: `@pb_2` ?: `@pb_3` ?: `@pb_4`)?.toDoubleOrNull()
}
```
**Strategy**: Uses first available buy price, converts string to number

```kotlin
fun getSellPrice(): Double? {
    return (`@ps_1` ?: `@ps_2` ?: `@ps_3` ?: `@ps_4`)?.toDoubleOrNull()
}
```
**Strategy**: Uses first available sell price, converts string to number

---

## 💡 Understanding Price Variants

### Why Multiple Variants?

Different price variants typically represent:

1. **Different Product Lines**
   - Variant 1: Premium gold (SJC brand)
   - Variant 2: Regular gold bars
   - Variant 3: Gold jewelry/rings
   - Variant 4: Silver products

2. **Different Locations**
   - Variant 1: Hanoi prices
   - Variant 2: Ho Chi Minh City prices
   - Variant 3: Da Nang prices
   - Variant 4: Other regions

3. **Different Weights**
   - Variant 1: 1 lượng (tael)
   - Variant 2: 5 chỉ (half tael)
   - Variant 3: 2 chỉ (small unit)
   - Variant 4: 1 chỉ (smallest unit)

---

## 📱 Display Example

With this data:
```json
{
  "@n_1": "Vàng SJC 1L",
  "@pb_1": "75500000",
  "@ps_1": "76500000",
  "@pt_1": "1 lượng",
  "@d_1": "24/04/2026"
}
```

Your app displays:
```
┌─────────────────┐
│ Vàng SJC 1L     │
│                 │
│ 💰 Precious     │
│    Metals       │
│                 │
│ Mua: ₫75,500,000│ (red)
│ Bán: ₫76,500,000│ (blue)
│ Trọng lượng:    │
│ 1 lượng         │
└─────────────────┘
```

---

## 🔄 Real-World Example

### Complete Product Entry

```xml
<row 
  @row="1"
  @n_1="Vàng SJC 1 lượng"
  @k_1="Vàng miếng"
  @pb_1="75500000"
  @ps_1="76500000"
  @pt_1="1 lượng (37.5g)"
  @d_1="24/04/2026 14:30"
  @n_2="Vàng SJC 5 chỉ"
  @pb_2="75400000"
  @ps_2="76400000"
  @pt_2="5 chỉ (18.75g)"
/>
```

**Interpreted as:**
- **Product**: Vàng SJC (SJC Brand Gold)
- **Category**: Vàng miếng (Gold Bar)
- **Buy Price**: 75,500,000 VND per 1 lượng
- **Sell Price**: 76,500,000 VND per 1 lượng
- **Weight**: 1 lượng (37.5 grams)
- **Last Update**: April 24, 2026 at 14:30

**Price Spread**: 
- Difference: 1,000,000 VND
- Percentage: ~1.3% markup

---

## 🎨 Customization Tips

### Display All Variants

Instead of showing only the first variant, you could display all available prices:

```kotlin
fun getAllPrices(): List<Pair<String, Double>> {
    val prices = mutableListOf<Pair<String, Double>>()
    
    `@pb_1`?.toDoubleOrNull()?.let { prices.add("Loại 1" to it) }
    `@pb_2`?.toDoubleOrNull()?.let { prices.add("Loại 2" to it) }
    `@pb_3`?.toDoubleOrNull()?.let { prices.add("Loại 3" to it) }
    `@pb_4`?.toDoubleOrNull()?.let { prices.add("Loại 4" to it) }
    
    return prices
}
```

### Add Price Comparison

```kotlin
fun getPriceSpread(): Double? {
    val buy = getBuyPrice()
    val sell = getSellPrice()
    return if (buy != null && sell != null) {
        sell - buy
    } else null
}
```

---

## 🔗 Related Resources

- **BTMC Official API Docs**: https://btmc.vn/thong-tin/tai-lieu-api/api-gia-vang-17784.html
- **BTMC Website**: https://btmc.vn
- **Currency Format**: Vietnamese Dong (₫)

---

## ⚠️ Important Notes

1. **Null Safety**: All fields are nullable - always handle null cases
2. **String to Number**: Prices come as strings, must convert to Double
3. **Multiple Variants**: Don't assume all variants are present
4. **Date Formats**: May vary, parse carefully
5. **Currency**: All prices are in VND (Vietnamese Dong)

---

**Last Updated**: April 24, 2026

