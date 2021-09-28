package Server.Program;



import Server.Server;
import Server.db.DbHandler;
import common.Exceptions.ProductNotFoundException;
import common.Task.Product;
import common.User;

import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class CollectionManager {
    private NavigableSet<Product> productCollection =  new TreeSet<>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private DbHandler dbHandler;

    public CollectionManager() {


    }
    public void addDbHandlerToCM() {
        dbHandler  = Server.getDbHandler();
    }
    /**
     * @return The collecton
     */
    public Set<Product> getCollection() {
        return productCollection;
    }

    /**
     * @return Last initialization time.
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * @return Last save time.
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }
    /**
     * @return Collection's type.
     */
    public String collectionType() {
        return productCollection.getClass().getName();
    }

    /**
     * @return Collection size.
     */
    public int collectionSize() {
        return productCollection.size();
    }

    /**
     * @param id ID of the product.
     * @return A product.
     */
    public Product getById(Long id) {
        for (Product product : productCollection) {
            if (product.getId().equals(id)) return product;
        }
        return null;
    }

    /**
     * @param prod Product object.
     * @return A product.
     */
    public Product getByValue(Product prod) {
        for (Product product : productCollection) {
            if (product.equals(prod)) return product;
        }
        return null;
    }

    /**
     * Add a new product to collection.
     * @param prod A product.
     */
    public void addToCollection(Product prod) {
        productCollection.add(prod);
    }

    /**
     * Remove a product from collection.
     * @param prod A product.
     */
    public void removeFromCollection(Product prod) {
        productCollection.remove(prod);
    }

    /**
     * Remove a product greater than the selected one.
     * @param prod A product to compare with.
     */
    public void removeGreater(Product prod, User user) {
        for (Product product : productCollection) {
            if (product.compareTo(prod) > 0) {
                Long id = prod.getId();
                Product productToRemove = getById(id);
                DbHandler dbHandler = Server.getDbHandler();
                dbHandler.removeFromDb(prod.getId().intValue(), user.getLogin());
                removeFromCollection(productToRemove);
            }
        }
    }
    /**
     * Remove a product lower than the selected one.
     * @param prod A product to compare with.
     */
    public void removeLower(Product prod, User user) {
        for (Product product : productCollection) {
            if (product.compareTo(prod) < 0) {
                Long id = prod.getId();
                Product productToRemove = getById(id);
                DbHandler dbHandler = Server.getDbHandler();
                dbHandler.removeFromDb(prod.getId().intValue(), user.getLogin());
                removeFromCollection(productToRemove);
            }
        }
    }

    /**
     * Clear collection.
     */
    public void clearCollection() {
        productCollection.clear();
    }

    /**
     * Generates next ID.
     * @return ID.
     */
    public Long generateNextId() {
        if (productCollection.isEmpty()) return 1L;
        return productCollection.last().getId() + 1;
    }

    /**
     * Saves the collection to file.
     */
    public void saveCollection() {
        /*fileManager.writeCollection(productCollection);*/
        lastSaveTime = LocalDateTime.now();
    }

    /**
     * Load the collection from file.
     */
    public void loadCollection() {
        dbHandler.loadCollectionFromDB();
        lastInitTime = LocalDateTime.now();
    }


    public int countByPrice(int arg){
        /*int count = 0;*/
        int count = (int) productCollection.stream().filter(ob -> ob.getPrice() == arg).count();
        /*for (Product prod : productCollection) {
            if (prod.getPrice() == arg) {
                count++;
            }
        }*/
        return count;
    }










    @Override
    public String toString() {
        if (productCollection.isEmpty()) return "Коллекция пуста!";

        String coll = "";
        for (Product product : productCollection) {
            coll += product.toString();
            if (product != productCollection.last()) coll += "\n\n";
        }
        return coll;
    }
}
