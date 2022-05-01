package com.spring.ecommerce.services;


import com.spring.ecommerce.domain.Product;
import com.spring.ecommerce.domain.users.Client;
import com.spring.ecommerce.exceptions.ProductHasAlreadyBeenSold;
import com.spring.ecommerce.exceptions.YouHaveAlreadyAddThisProductInYourWishlistException;
import com.spring.ecommerce.repositories.ClientRepository;
import com.spring.ecommerce.repositories.ProductRepository;
import com.spring.ecommerce.security.ClientSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class WishlistService {

	@Autowired
	private ClientService clientService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ClientRepository clientRepo;

	@Autowired
	private ProductRepository productRepo;

	private Client findClientById(Integer id) {
		return clientService.findById(id);
	}

	private Product findProductById(Integer id) {
		return productService.findById(id);
	}

	public Set<Product> findAll() {
		ClientSS user = UserService.clientAuthenticated();
		Client cli = findClientById(user.getId());

		return cli.getProductsWished();

	}

	public void markProductAsWished(Integer productId) {
		Product product = findProductById(productId);
		ClientSS user = UserService.clientAuthenticated();
		Client client = findClientById(user.getId());

		if (Product.isSold(product)) {
			throw new ProductHasAlreadyBeenSold();
		}

		if (client.getProductsWished().contains(product)) {
			throw new YouHaveAlreadyAddThisProductInYourWishlistException();
		}

		client.getProductsWished().add(product);
		product.getWhoWhishesThisProduct().add(client);

		clientRepo.save(client);
		productRepo.save(product);
	}

	@Transactional
	public void delete(Integer productId) {
		ClientSS user = UserService.clientAuthenticated();

		productRepo.removeFromClientWishlist(productId, user.getId());
	}
	
	@Transactional
	public void removeProductFromWishlistWhenIsSold(Integer productId) {
		productRepo.removeFromWishListWhenIsSold(productId);
	}
}
