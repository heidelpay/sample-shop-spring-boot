package com.heidelpay.samples.shop.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.heidelpay.samples.shop.dao.CustomerRepository;
import com.heidelpay.samples.shop.model.Customer;


@Controller
@RequestMapping("customer")
public class CustomerController {

	@Autowired CustomerRepository dao;
	
	@GetMapping("{id}")
	public ModelAndView registerView() {
		Map<String, Object> map = new HashMap<>();
		return new ModelAndView("customer/form", map);
	}
	
	@PostMapping("/")
	public RedirectView create(@ModelAttribute Customer customer) {
		customer = dao.save(customer);
		return new RedirectView("/customer");
	}
	
	@PutMapping("{id}")
	public RedirectView update(@ModelAttribute Customer customer) {
		dao.save(customer);
		return new RedirectView("/customer/" + customer.getId());
	}
	
	@PostMapping("{id}/delete")
	public RedirectView delete(@PathVariable Long id) {
		Customer customer = dao.getOne(id);
		if(customer != null) {
			dao.delete(customer);
		}
		return new RedirectView("/customer");
	}
	
	
	@GetMapping("")
	public ModelAndView index() {
		Map<String, Object> model = new HashMap<>();
		model.put("customers", dao.findAll());
		return new ModelAndView("customer/index.html", model);
	}
	
}
