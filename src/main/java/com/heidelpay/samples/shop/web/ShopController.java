package com.heidelpay.samples.shop.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.heidelpay.samples.shop.dao.ProductRepository;
import com.heidelpay.samples.shop.model.Product;

/**
 * The shop...
 */
@Controller
@RequestMapping("")
public class ShopController {

	@Autowired ProductRepository productDao;
	@Autowired BasketHolder basketHolder;
	
	@RequestMapping("")
	public ModelAndView index() {
		
		Map<String, Object> model = new HashMap<>();
		model.put("products", getRandomProducts(6));
		model.put("featured", getRandomProducts(3));
		
		return new ModelAndView("shop/index.html", model);
	}
	
	@PostMapping("/basket/add")
	public String add(@RequestParam("productId") Long productId) {
		basketHolder.add(productDao.getOne(productId), 1);
		return "redirect:/";
	}
	
	private List<Product> getRandomProducts(int amount) {
		List<Product> all = productDao.findAll();
		Collections.shuffle(all);
		return all.subList(0,  amount-1);
	}
	
	
}
