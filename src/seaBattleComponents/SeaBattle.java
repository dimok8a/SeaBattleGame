package seaBattleComponents;

import java.util.ArrayList;

public class SeaBattle {
    public ArrayList<ArrayList<ECell>> field;
    private int size;
    public ArrayList<Ship> ships;
    // Нерасставленные корабли
    public ArrayList<Ship> stockShips;
    // Геттеры
    public ArrayList<ArrayList<ECell>> getField() {
        return field;
    }
    public int getSize() {
        return size;
    }
    public ArrayList<Ship> getShips() {
        return ships;
    }

    // Сеттеры
    public void setField(ArrayList<ArrayList<ECell>> field) {
        this.field = field;
    }
    public void setSize(int size) {
        this.size = Math.max(size, 10);
    }
    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    // Заполнение кораблей в запасе
    private void fillStockShips() {
        this.stockShips.add(new Ship(4, 0, 0, EDirection.Down, 10));

        this.stockShips.add(new Ship(3, 0, 0, EDirection.Down, 8));
        this.stockShips.add(new Ship(3, 0, 0, EDirection.Down, 9));

        this.stockShips.add(new Ship(2, 0, 0, EDirection.Down, 5));
        this.stockShips.add(new Ship(2, 0, 0, EDirection.Down, 6));
        this.stockShips.add(new Ship(2, 0, 0, EDirection.Down, 7));

        this.stockShips.add(new Ship(1, 0, 0, EDirection.Down, 1));
        this.stockShips.add(new Ship(1, 0, 0, EDirection.Down, 2));
        this.stockShips.add(new Ship(1, 0, 0, EDirection.Down, 3));
        this.stockShips.add(new Ship(1, 0, 0, EDirection.Down, 4));
    }
    // Конструктор
    public SeaBattle(int size) {
        ArrayList<ArrayList<ECell>> newField = new ArrayList<ArrayList<ECell>>(size);
        // Заполняем все клетки нулевыми
        for (int i = 0; i<size; i++) {
            ArrayList<ECell> newStroke = new ArrayList<ECell>(size);
            for (int j = 0; j<size; j++) {
                newStroke.add(ECell.Zero);
            }
            newField.add(newStroke);
        }
        setSize(size);
        setField(newField);
        setShips(new ArrayList<Ship>(0));
        stockShips = new ArrayList<>();
        fillStockShips();
    }
    // Сколько кораблей в хранилище с переданным размером
    public int countShipsBySize(int size) {
        int count = 0;
        for (Ship ship: ships) {
            if (ship.getSize() == size)
                count+=1;
        }
        return count;
    }

    // Существует ли в хранилище корабль с переданным id
    public boolean doesShipExists(int id) {
        for (Ship ship:ships)
            if (ship.getId() == id)
                return true;
        return false;
    }

    // Получить корабль по его id
    public Ship getShipById(int id) {
        for (Ship ship:ships)
            if (ship.getId() == id)
                return ship;
        return new Ship(0, 0);
    }

    // Добавить корабль на поле
    public boolean addShip(Ship ship) {
        // Проверяем, набрано ли достаточно кораблей данного размера
        if (countShipsBySize(ship.getSize())-1 == 4-ship.getSize()) {
            System.out.println("Превышен лимит кораблей размера " + ship.getSize());
            return false;
        }
        // Проверяем, нет ли корабля с таким же id
        if (doesShipExists(ship.getId())) {
            System.out.println("Корабль с id " + ship.getId() + " уже существует");
            return false;
        }
        // Ставим на поле, если возможно
        if (ship.canPutShip(field)) {
            ship.addShipOnField(field);
            ships.add(ship);
            return true;
        }
        else {
            System.out.println("Корабль " + ship.getId() + " нельзя сюда поставить");
            return false;
        }
    }

    // Удаление корабля с поля
    public void deleteShip(int id) {
        if (doesShipExists(id)){
            Ship ship = getShipById(id);
            ship.deleteShipFromField(field);
            ships.remove(ship);
        }
    }

    // Меняет оккупированные клетки на обычные
    public void readyForGame() {
        for (int i = 0; i<size; i++)
            for (int j = 0; j<size; j++)
                if (field.get(i).get(j) == ECell.Occupied)
                    field.get(i).set(j, ECell.Zero);
    }

    // Выстрел на поле
    public ShotStatus takeShot(int x, int y) {
        // Если в переданных координатах корабль или его часть, то находим корабль, лежащий в клетке
        if (field.get(y).get(x) == ECell.Ship) {
            String status = "";
            for (int i = 0; i<this.ships.size(); i++) {
                Ship ship = this.ships.get(i);
                if (ship.isShipInCage(x, y)) {
                    // Уменьшаем размер целой части корабля
                    ship.getHit();
                    if (ship.getActualSize() == 0) {
                        ship.setOccupiedZone(field);
                        ships.remove(ship);
                        status = "Убил";
                    }
                    else
                        status = "Ранен";
                    field.get(y).set(x, ECell.ShipHit);
                }
            }
            return new ShotStatus(true, status);
        } else {
            if (field.get(y).get(x) == ECell.Zero)
                field.get(y).set(x, ECell.OffTarget);
            return new ShotStatus(false, "Мимо");
        }
    }




    @Override
    public String toString() {
        StringBuilder returnStr = new StringBuilder("Поле: \n");
        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++) {
                if (field.get(i).get(j) == ECell.Zero) returnStr.append("0 ");
                if (field.get(i).get(j) == ECell.Ship) returnStr.append("0 ");
                if (field.get(i).get(j) == ECell.OffTarget) returnStr.append("· ");
                if (field.get(i).get(j) == ECell.ShipHit) returnStr.append("X ");
                if (field.get(i).get(j) == ECell.Occupied) returnStr.append("· ");
            }
            returnStr.append("\n");
        }
        return returnStr.toString();
    }
}
