package com.example.WebApp.web;

import com.example.WebApp.entity.CartItem;
import com.example.WebApp.entity.CustomerOrder;
import com.example.WebApp.entity.DeliveryAddress;
import com.example.WebApp.entity.Registration;
import com.example.WebApp.repository.CustomerOrderRepository;
import com.example.WebApp.repository.DeliveryAddressRepository;
import com.example.WebApp.repository.RegistrationRepository;
import com.example.WebApp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class CartController {

    @Autowired
    DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private RegistrationRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final Map<String, Map<String, Double>> PRODUCT_PRICES = new HashMap<>();

    static {
        Map<String, Double> softSconesPrices = new HashMap<>();
        softSconesPrices.put("10L", 400.0);
        softSconesPrices.put("20L", 650.0);
        softSconesPrices.put("5L", 220.0);
        PRODUCT_PRICES.put("Soft Scones", softSconesPrices);

        Map<String, Double> strawberryLoversPrices = new HashMap<>();
        strawberryLoversPrices.put("10L", 450.0);
        strawberryLoversPrices.put("20L", 800.0);
        strawberryLoversPrices.put("5L", 260.0);
        PRODUCT_PRICES.put("Strawberry Lovers", strawberryLoversPrices);

        Map<String, Double> coconutBiscuitsPrices = new HashMap<>();
        coconutBiscuitsPrices.put("10L", 350.0);
        coconutBiscuitsPrices.put("20L", 600.0);
        coconutBiscuitsPrices.put("5L", 200.0);
        PRODUCT_PRICES.put("Coconut Biscuits", coconutBiscuitsPrices);

        Map<String, Double> assortedBiscuitsPrices = new HashMap<>();
        assortedBiscuitsPrices.put("10L", 600.0);
        assortedBiscuitsPrices.put("20L", 1000.0);
        assortedBiscuitsPrices.put("5L", 350.0);
        PRODUCT_PRICES.put("Assorted Biscuits", assortedBiscuitsPrices);

        Map<String, Double> gingerBiscuitsPrices = new HashMap<>();
        gingerBiscuitsPrices.put("10L", 350.0);
        gingerBiscuitsPrices.put("20L", 600.0);
        gingerBiscuitsPrices.put("5L", 200.0);
        PRODUCT_PRICES.put("Ginger Biscuits", gingerBiscuitsPrices);

        Map<String, Double> romanyCreamPrices = new HashMap<>();
        romanyCreamPrices.put("10L", 450.0);
        romanyCreamPrices.put("20L", 800.0);
        romanyCreamPrices.put("5L", 260.0);
        PRODUCT_PRICES.put("Romany Cream", romanyCreamPrices);
    }

    @RequestMapping("/")
    public String homePage(Model model, HttpSession session) {
        Registration user = (Registration) session.getAttribute("user");
        model.addAttribute("user", user);
        return user == null ? "index.html" : "home.html";
    }

    @RequestMapping("/cart.html")
    public String cartPage(HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        Double totalPrice = (Double) session.getAttribute("totalPrice");
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart.html";
    }

    @RequestMapping("/products.html")
    public String productsPage() {
        return "cart.html";
    }

    @RequestMapping("/regi.html")
    public String regiPage() {
        return "regi.html";
    }

    @RequestMapping("/login.html")
    public String loginPage() {
        return "login.html";
    }

    @RequestMapping("/about.html")
    public String aboutPage() {
        return "about.html";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("product_name") String productName,
                            @RequestParam("size") String size,
                            Model model,
                            HttpServletRequest request) {
        double price = PRODUCT_PRICES.getOrDefault(productName, new HashMap<>()).getOrDefault(size, 0.0);

        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Check if the item already exists in the cart
        boolean itemExists = false;
        for (CartItem item : cartItems) {
            if (item.getProductName().equals(productName) && item.getSize().equals(size)) {
                // If the item exists, increment its quantity
                item.setQuantity(item.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }

        // If the item doesn't exist, add it to the cart
        if (!itemExists) {
            CartItem item = new CartItem();
            item.setImageUrl("assest/images/" + productName + ".jpg");
            item.setProductName(productName);
            item.setSize(size);
            item.setPrice(price);
            item.setQuantity(1);
            cartItems.add(item);
        }

        // Update the total price
        double totalPrice = cartItems.stream().mapToDouble(ci -> ci.getPrice() * ci.getQuantity()).sum();

        session.setAttribute("cartItems", cartItems);
        session.setAttribute("totalPrice", totalPrice);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "view_cart.html";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("index") int index,
                                 Model model,
                                 HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            CartItem item = cartItems.remove(index);
            double totalPrice = (double) session.getAttribute("totalPrice");
            totalPrice -= item.getPrice() * item.getQuantity();

            session.setAttribute("cartItems", cartItems);
            session.setAttribute("totalPrice", totalPrice);

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);
        }
        return "view_cart.html";
    }

    @RequestMapping("/view-cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        Double totalPrice = (Double) session.getAttribute("totalPrice");
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "view_cart.html";
    }

    // Constructor injection for the repository


    @PostMapping("/deliverOrder")
    public String deliver(HttpSession session, Model model) {
        Registration user = (Registration) session.getAttribute("user");

        if (user != null) {
            Optional<DeliveryAddress> deliveryAddressOptional = deliveryAddressRepository.findByUserId(user.getId());

            if (deliveryAddressOptional.isPresent()) {
                List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
                model.addAttribute("cartItems", cartItems);
                return "deliver_done.html";
            } else {
                return "address.html"; // Redirect to address registration page
            }
        } else {
            return "login"; // Redirect to login page
        }
    }


    @PostMapping("/address")
    public String saveAddress(
            @RequestParam("street") String streetName,
            @RequestParam("suburb") String suburb,
            @RequestParam("city") String city,
            @RequestParam("province") String province,
            @RequestParam("postal") String postal,
            HttpSession session, Model model
    ) {
        // Retrieve the user from the session
        String userEmail = (String) session.getAttribute("email");
        Registration user = userRepository.findByEmail(userEmail);

        // Check if the delivery address already exists
        Optional<DeliveryAddress> optionalAddress = deliveryAddressRepository.findByStreetAndSuburbAndCityAndProvinceAndPostalCode(
                streetName, suburb, city, province, postal);

        DeliveryAddress da;
        if (optionalAddress.isPresent()) {
            // If the address exists, use it
            da = optionalAddress.get();
        } else {
            // If not, create a new one
            da = new DeliveryAddress();
            da.setStreet(streetName);
            da.setCity(city);
            da.setPostalCode(postal);
            da.setProvince(province);
            da.setSuburb(suburb);
            // Associate the user with the address
            da.setUser(user);
            // Save the address
            deliveryAddressRepository.save(da);
        }

        // Retrieve cart items and total price from the session
        List<CartItem> myCart = (List<CartItem>) session.getAttribute("cartItems");
        double totPrice = (Double) session.getAttribute("totalPrice");

        // Create a new customer order
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setOrderDate(new Date());
        customerOrder.setDeliveryAddress(da);
        customerOrder.setItems(myCart);
        for (CartItem x : myCart) {
            customerOrder.setQuantity(x.getQuantity());
        }
        customerOrder.setTotalPrice(totPrice);
        customerOrder.setCustomer(user);
        // Calculate delivery date (assuming 3 days from now)
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 3);
        date = c.getTime();
        customerOrder.setDeliveryDate(date);

        // Save the customer order
        customerOrderRepository.save(customerOrder);

        // Send order details emails
        emailService.orderDetails(myCart, user, totPrice);
        emailService.orderDetailsCustomer(myCart, user, totPrice);

        // Clear cart items from the session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        cartItems.clear();
        model.addAttribute("cartItems", cartItems);

        // Return the view
        return "deliver_done.html";
    }





    @PostMapping("/checkout")
    public String checkoutCart(HttpServletRequest request) throws MessagingException {
        HttpSession session = request.getSession();
        Boolean active = (Boolean) session.getAttribute("active");
        if (active != null && active) {
            // Checkout logic
            return "collect.html";
        } else {
            return "login";
        }
    }
    @PostMapping("/increaseQuantity")
    public String increaseQuantity(@RequestParam("index") int index,
                                   HttpSession session,
                                   Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            CartItem item = cartItems.get(index);
            item.setQuantity(item.getQuantity() + 1);
            session.setAttribute("cartItems", cartItems);
            session.setAttribute("totalPrice", calculateTotalPrice(cartItems));
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", calculateTotalPrice(cartItems));
        }
        return "view_cart.html";
    }

    @PostMapping("/decreaseQuantity")
    public String decreaseQuantity(@RequestParam("index") int index,
                                   HttpSession session,
                                   Model model) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null && index >= 0 && index < cartItems.size()) {
            CartItem item = cartItems.get(index);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                session.setAttribute("cartItems", cartItems);
                session.setAttribute("totalPrice", calculateTotalPrice(cartItems));
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalPrice", calculateTotalPrice(cartItems));
            }
        }
        return "view_cart.html";
    }

    // Helper method to calculate total price
    private double calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
