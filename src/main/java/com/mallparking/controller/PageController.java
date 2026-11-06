package com.mallparking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Controller mapping every sidebar navigation route except {@code GET /}.
 *
 * <p>Each method populates the model with:</p>
 * <ul>
 *   <li>{@code pageTitle}  – displayed in the topbar and browser tab</li>
 *   <li>{@code currentPage} – used by the sidebar fragment to highlight the
 *       active nav link via {@code th:classappend}</li>
 *   <li>Page-specific sample data attributes (ready to be replaced by
 *       real service calls once the service/repository layers are built)</li>
 * </ul>
 *
 * <p>Routes handled:
 * <pre>
 *   GET /slots     → slots.html
 *   GET /vehicles  → vehicles.html
 *   GET /activity  → activity.html
 *   GET /revenue   → revenue.html
 *   GET /reports   → reports.html
 *   GET /floors    → floors.html
 *   GET /settings  → settings.html
 * </pre>
 * </p>
 */
@Controller
public class PageController {

    // ── /slots ────────────────────────────────────────────────────────────────

    @GetMapping("/slots")
    public String slots(Model model) {
        model.addAttribute("pageTitle",   "Parking Slots");
        model.addAttribute("currentPage", "slots");

        // Sample slot rows: [slotId, floor, row, status, vehicleNo, since]
        model.addAttribute("slots", List.of(
                Map.of("slotId","B1-001","floor","B1","status","OCCUPIED","vehicleNo","MH12 AB 4521","since","08:14 AM","duration","2h 38m"),
                Map.of("slotId","B1-002","floor","B1","status","OCCUPIED","vehicleNo","KA09 CD 7823","since","09:02 AM","duration","1h 50m"),
                Map.of("slotId","B1-003","floor","B1","status","AVAILABLE","vehicleNo","-","since","-","duration","-"),
                Map.of("slotId","B1-004","floor","B1","status","OCCUPIED","vehicleNo","DL01 EF 1190","since","07:45 AM","duration","3h 07m"),
                Map.of("slotId","B1-005","floor","B1","status","RESERVED","vehicleNo","GJ05 IJ 9901","since","10:00 AM","duration","-"),
                Map.of("slotId","B2-001","floor","B2","status","OCCUPIED","vehicleNo","TN22 GH 3356","since","09:30 AM","duration","1h 22m"),
                Map.of("slotId","B2-002","floor","B2","status","AVAILABLE","vehicleNo","-","since","-","duration","-"),
                Map.of("slotId","B2-003","floor","B2","status","OCCUPIED","vehicleNo","UP78 KL 6612","since","08:55 AM","duration","1h 57m"),
                Map.of("slotId","GF-001","floor","GF","status","OCCUPIED","vehicleNo","MH14 MN 2278","since","10:05 AM","duration","47m"),
                Map.of("slotId","GF-002","floor","GF","status","AVAILABLE","vehicleNo","-","since","-","duration","-"),
                Map.of("slotId","GF-003","floor","GF","status","OCCUPIED","vehicleNo","RJ15 OP 8843","since","09:48 AM","duration","1h 04m"),
                Map.of("slotId","GF-004","floor","GF","status","AVAILABLE","vehicleNo","-","since","-","duration","-")
        ));

        model.addAttribute("totalSlots",     450);
        model.addAttribute("occupiedSlots",  312);
        model.addAttribute("availableSlots", 138);
        model.addAttribute("reservedSlots",    5);

        return "slots";
    }

    // ── /vehicles ─────────────────────────────────────────────────────────────

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("pageTitle",   "Vehicles");
        model.addAttribute("currentPage", "vehicles");

        model.addAttribute("vehicles", List.of(
                Map.of("vehicleNo","MH12 AB 4521","driver","Rahul Sharma","floor","B1","slot","B1-001","entryTime","08:14 AM","status","PARKED","amountDue","₹130"),
                Map.of("vehicleNo","KA09 CD 7823","driver","Priya Nair","floor","B1","slot","B1-002","entryTime","09:02 AM","status","PARKED","amountDue","₹90"),
                Map.of("vehicleNo","DL01 EF 1190","driver","Amit Verma","floor","B1","slot","B1-004","entryTime","07:45 AM","status","PARKED","amountDue","₹155"),
                Map.of("vehicleNo","GJ05 IJ 9901","driver","Ravi Patel","floor","GF","slot","GF-027","entryTime","10:29 AM","status","PARKED","amountDue","₹30"),
                Map.of("vehicleNo","TN22 GH 3356","driver","Sneha Pillai","floor","B1","slot","B1-105","entryTime","09:30 AM","status","EXITED","amountDue","₹65"),
                Map.of("vehicleNo","UP78 KL 6612","driver","Anjali Singh","floor","B2","slot","B2-091","entryTime","08:55 AM","status","EXITED","amountDue","₹95"),
                Map.of("vehicleNo","MH14 MN 2278","driver","Suresh Kumar","floor","B1","slot","B1-019","entryTime","10:15 AM","status","PARKED","amountDue","₹45"),
                Map.of("vehicleNo","RJ15 OP 8843","driver","Meera Joshi","floor","GF","slot","GF-003","entryTime","10:08 AM","status","EXITED","amountDue","₹52"),
                Map.of("vehicleNo","HR26 QR 5544","driver","Deepak Rawat","floor","B2","slot","B2-033","entryTime","07:20 AM","status","PARKED","amountDue","₹185"),
                Map.of("vehicleNo","PB10 ST 2211","driver","Kavya Reddy","floor","GF","slot","GF-018","entryTime","09:11 AM","status","PARKED","amountDue","₹100")
        ));

        model.addAttribute("totalParked",   312);
        model.addAttribute("totalExited",    87);
        model.addAttribute("carsToday",     187);
        model.addAttribute("avgDuration",   "1h 42m");

        return "vehicles";
    }

    // ── /activity ─────────────────────────────────────────────────────────────

    @GetMapping("/activity")
    public String activity(Model model) {
        model.addAttribute("pageTitle",   "Entry / Exit Log");
        model.addAttribute("currentPage", "activity");

        model.addAttribute("logs", List.of(
                Map.of("time","10:52 AM","event","ENTRY","vehicleNo","MH12 AB 4521","driver","Rahul Sharma","floor","B1","slot","B1-038","duration","-","amount","-"),
                Map.of("time","10:48 AM","event","EXIT", "vehicleNo","KA09 CD 7823","driver","Priya Nair",  "floor","GF","slot","GF-012","duration","1h 46m","amount","₹88"),
                Map.of("time","10:41 AM","event","ENTRY","vehicleNo","DL01 EF 1190","driver","Amit Verma",  "floor","B2","slot","B2-074","duration","-","amount","-"),
                Map.of("time","10:35 AM","event","EXIT", "vehicleNo","TN22 GH 3356","driver","Sneha Pillai","floor","B1","slot","B1-105","duration","2h 05m","amount","₹105"),
                Map.of("time","10:29 AM","event","ENTRY","vehicleNo","GJ05 IJ 9901","driver","Ravi Patel",  "floor","GF","slot","GF-027","duration","-","amount","-"),
                Map.of("time","10:22 AM","event","EXIT", "vehicleNo","UP78 KL 6612","driver","Anjali Singh","floor","B2","slot","B2-091","duration","1h 27m","amount","₹73"),
                Map.of("time","10:15 AM","event","ENTRY","vehicleNo","MH14 MN 2278","driver","Suresh Kumar","floor","B1","slot","B1-019","duration","-","amount","-"),
                Map.of("time","10:08 AM","event","EXIT", "vehicleNo","RJ15 OP 8843","driver","Meera Joshi", "floor","GF","slot","GF-003","duration","52m","amount","₹52"),
                Map.of("time","09:55 AM","event","ENTRY","vehicleNo","HR26 QR 5544","driver","Deepak Rawat","floor","B2","slot","B2-033","duration","-","amount","-"),
                Map.of("time","09:48 AM","event","EXIT", "vehicleNo","PB10 ST 2211","driver","Kavya Reddy", "floor","GF","slot","GF-018","duration","38m","amount","₹38"),
                Map.of("time","09:40 AM","event","ENTRY","vehicleNo","WB12 UV 3399","driver","Arjun Das",   "floor","B1","slot","B1-072","duration","-","amount","-"),
                Map.of("time","09:31 AM","event","EXIT", "vehicleNo","AP09 WX 7700","driver","Lakshmi Iyer","floor","B2","slot","B2-055","duration","3h 11m","amount","₹155"),
                Map.of("time","09:22 AM","event","ENTRY","vehicleNo","CG07 YZ 1122","driver","Vikram Negi", "floor","GF","slot","GF-009","duration","-","amount","-"),
                Map.of("time","09:14 AM","event","EXIT", "vehicleNo","JK02 AA 6688","driver","Pooja Ghosh", "floor","B1","slot","B1-044","duration","1h 54m","amount","₹97"),
                Map.of("time","09:05 AM","event","ENTRY","vehicleNo","OD05 BB 4433","driver","Rahul Tiwari","floor","B2","slot","B2-016","duration","-","amount","-")
        ));

        model.addAttribute("totalEntries",  104);
        model.addAttribute("totalExits",     87);
        model.addAttribute("pendingInside", 312);

        return "activity";
    }

    // ── /revenue ──────────────────────────────────────────────────────────────

    @GetMapping("/revenue")
    public String revenue(Model model) {
        model.addAttribute("pageTitle",   "Revenue");
        model.addAttribute("currentPage", "revenue");

        model.addAttribute("todayRevenue",   "₹10,920");
        model.addAttribute("weekRevenue",    "₹68,450");
        model.addAttribute("monthRevenue",   "₹2,84,300");
        model.addAttribute("avgDaily",       "₹9,776");

        model.addAttribute("transactions", List.of(
                Map.of("time","10:48 AM","vehicleNo","KA09 CD 7823","floor","GF","duration","1h 46m","amount","₹88","mode","UPI"),
                Map.of("time","10:35 AM","vehicleNo","TN22 GH 3356","floor","B1","duration","2h 05m","amount","₹105","mode","Cash"),
                Map.of("time","10:22 AM","vehicleNo","UP78 KL 6612","floor","B2","duration","1h 27m","amount","₹73","mode","Card"),
                Map.of("time","10:08 AM","vehicleNo","RJ15 OP 8843","floor","GF","duration","52m","amount","₹52","mode","UPI"),
                Map.of("time","09:48 AM","vehicleNo","PB10 ST 2211","floor","GF","duration","38m","amount","₹38","mode","UPI"),
                Map.of("time","09:31 AM","vehicleNo","AP09 WX 7700","floor","B2","duration","3h 11m","amount","₹155","mode","Cash"),
                Map.of("time","09:14 AM","vehicleNo","JK02 AA 6688","floor","B1","duration","1h 54m","amount","₹97","mode","Card"),
                Map.of("time","08:52 AM","vehicleNo","MH01 CC 9988","floor","B1","duration","4h 22m","amount","₹218","mode","Card"),
                Map.of("time","08:34 AM","vehicleNo","KL08 DD 5566","floor","B2","duration","3h 46m","amount","₹188","mode","UPI"),
                Map.of("time","08:10 AM","vehicleNo","TG11 EE 2244","floor","GF","duration","2h 50m","amount","₹142","mode","Cash")
        ));

        return "revenue";
    }

    // ── /reports ──────────────────────────────────────────────────────────────

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("pageTitle",   "Reports");
        model.addAttribute("currentPage", "reports");

        model.addAttribute("peakHour",       "11:00 AM – 12:00 PM");
        model.addAttribute("peakOccupancy",  "91%");
        model.addAttribute("avgTurnover",    "4.2 vehicles/slot/day");
        model.addAttribute("totalVehicles",  "187");

        // Weekly occupancy % (Mon–Sun)
        model.addAttribute("weeklyOccupancy", List.of(62, 74, 68, 85, 91, 88, 70));
        // Weekly revenue (Mon–Sun)
        model.addAttribute("weeklyRevenue",   List.of(8400, 10200, 9150, 11800, 12600, 11900, 8920));

        return "reports";
    }

    // ── /floors ───────────────────────────────────────────────────────────────

    @GetMapping("/floors")
    public String floors(Model model) {
        model.addAttribute("pageTitle",   "Floors");
        model.addAttribute("currentPage", "floors");

        model.addAttribute("floors", List.of(
                Map.of("code","B1","name","Basement Floor 1","total",150,"occupied",138,"available",12,"pct",92.0,"status","FULL",   "rows",5,"cols",30),
                Map.of("code","B2","name","Basement Floor 2","total",150,"occupied",112,"available",38,"pct",74.7,"status","HIGH",    "rows",5,"cols",30),
                Map.of("code","GF","name","Ground Floor",    "total",150,"occupied", 62,"available",88,"pct",41.3,"status","MODERATE","rows",5,"cols",30)
        ));

        return "floors";
    }

    // ── /settings ─────────────────────────────────────────────────────────────

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("pageTitle",   "Settings");
        model.addAttribute("currentPage", "settings");
        return "settings";
    }
}
