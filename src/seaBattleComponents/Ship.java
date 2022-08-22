package seaBattleComponents;

import java.util.ArrayList;

public class Ship {
    // Размер корабля (фиксированный)
    private int size;
    // Начальные координаты
    private int startX;
    private int startY;
    // Направление (только вниз или вправо)
    private EDirection direction;
    // Размер целой части (не подбитой)
    private int actualSize;
    // Идентификатор
    private int id;
    // Сеттеры
    public void setSize(int size) {
        this.size = size;
    }
    public void setStartX(int startX) {
        this.startX = startX;
    }
    public void setStartY(int startY) {
        this.startY = startY;
    }
    public void setDirection(EDirection direction) {
        this.direction = direction;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setActualSize(int actualSize) {
        this.actualSize = actualSize;
    }

    // Геттеры
    public int getSize() {
        return size;
    }
    public int getStartX() {
        return startX;
    }
    public int getStartY() {
        return startY;
    }
    public EDirection getDirection() {
        return direction;
    }
    public int getId() {
        return id;
    }
    public int getActualSize() {
        return actualSize;
    }

    // Конструкторы
    public Ship(int size, int startX, int startY, EDirection direction, int id) {
        setSize(size);
        setActualSize(size);
        setStartX(startX);
        setStartY(startY);
        setDirection(direction);
        setId(id);
    }
    public Ship(int size, int id) {
        setSize(size);
        setActualSize(size);
        setId(id);
        setStartX(-1);
        setStartY(-1);
        setDirection(EDirection.Down);
    }

    public void changeDirection() {
        if (this.direction == EDirection.Right)
            this.direction = EDirection.Down;
        else
            this.direction = EDirection.Right;
    }

    // Попадает ли корабль в клетку с переданными координатами
    public boolean isShipInCage(int cageX, int cageY) {
        if (direction == EDirection.Right) {
            if (cageY != startY)
                return false;
            for (int i = startX; i<startX+size; i++){
                if (i == cageX)
                    return true;
            }
        } else {
            if (cageX != startX)
                return false;
            for (int i = startY; i<startY+size; i++){
                if (i == cageY)
                    return true;
            }
        }
        return false;
    }

    // Можно ли поставить корабль с его координатами, размером и направлением на переданное поле
    public boolean canPutShip(ArrayList<ArrayList<ECell>> field) {
        if (direction == EDirection.Right) {
            if (startX + size > field.size())
                return false;
            for (int i = startX; i < startX + size; i++)
                if (!(field.get(startY).get(i) == ECell.Zero || field.get(startY).get(i) == ECell.Highlight))
                    return false;
        } else {
            if (startY + size > field.size())
                return false;
            for (int i = startY; i<startY +size; i++)
                if (!(field.get(i).get(startX) == ECell.Zero || field.get(i).get(startX) == ECell.Highlight))
                    return false;
        }
        return true;
    }

    // Смена клеток переданного поля (добавление корабля на поле)
    public void addShipOnField(ArrayList<ArrayList<ECell>> field) {
        if (direction == EDirection.Right)
            for (int i = startX; i<startX+size; i++)
                field.get(startY).set(i, ECell.Ship);
        else
            for (int i = startY; i<startY+size; i++)
                field.get(i).set(startX, ECell.Ship);
        setOccupiedZone(field);
    }

    // Закрашиваем зону вокруг корабля (чтобы нельзя было поставить другие корабли)
    public void setOccupiedZone(ArrayList<ArrayList<ECell>> field) {
        if (direction == EDirection.Right) {
            for (int i = startX; i<startX+size; i++) {
                // Занимаем клетки выше и ниже корабля
                if (startY + 1 < field.size())
                    field.get(startY+1).set(i, ECell.Occupied);
                if (startY - 1 >= 0)
                    field.get(startY - 1).set(i, ECell.Occupied);
            }
            // Занимаем клетку справа
            if (startX + size < field.size()) {
                field.get(startY).set(startX + size, ECell.Occupied);
                // Диагонали
                if (startY + 1 < field.size())
                    field.get(startY+1).set(startX + size, ECell.Occupied);
                if (startY - 1 >= 0)
                    field.get(startY - 1).set(startX + size, ECell.Occupied);
            }
            // Клетку слева
            if (startX - 1 >= 0) {
                field.get(startY).set(startX - 1, ECell.Occupied);
                // Диагонали
                if (startY + 1 < field.size())
                    field.get(startY+1).set(startX - 1, ECell.Occupied);
                if (startY - 1 >= 0)
                    field.get(startY - 1).set(startX - 1, ECell.Occupied);
            }

        } else {
            for (int i = startY; i<startY+size; i++){
                // Занимаем клетки левее и правее корабля
                if (startX + 1 < field.size())
                    field.get(i).set(startX + 1, ECell.Occupied);
                if (startX - 1 >= 0)
                    field.get(i).set(startX - 1, ECell.Occupied);
            }
            // Клетку сверху
            if (startY + size < field.size()) {
                field.get(startY + size).set(startX, ECell.Occupied);
                // Диагонали
                if (startX + 1 < field.size())
                    field.get(startY + size).set(startX + 1, ECell.Occupied);
                if (startX - 1 >= 0)
                    field.get(startY + size).set(startX - 1, ECell.Occupied);
            }
            // Клетку снизу
            if (startY - 1 >= 0) {
                field.get(startY - 1).set(startX, ECell.Occupied);
                // Диагонали
                if (startX + 1 < field.size())
                    field.get(startY - 1).set(startX + 1, ECell.Occupied);
                if (startX - 1 >= 0)
                    field.get(startY - 1).set(startX - 1, ECell.Occupied);
            }
        }
    }
    // Удаление корабля с поля (очищаем место, где стоял корабль, а также зону вокруг)
    public void deleteShipFromField(ArrayList<ArrayList<ECell>> field) {
        if (direction == EDirection.Right) {
            for (int i = startX; i<startX+size; i++) {
                field.get(startY).set(i, ECell.Zero);
                // Занимаем клетки выше и ниже корабля
                if (startY + 1 < field.size())
                    field.get(startY+1).set(i, ECell.Zero);
                if (startY - 1 >= 0)
                    field.get(startY - 1).set(i, ECell.Zero);
            }
            if (startX + size < field.size()) {
                field.get(startY).set(startX + size, ECell.Zero);
                // Диагонали
                if (startY + 1 < field.size())
                    field.get(startY+1).set(startX + size, ECell.Zero);
                if (startY - 1 >= 0)
                    field.get(startY - 1).set(startX + size, ECell.Zero);
            }
            if (startX - 1 >= 0) {
                field.get(startY).set(startX - 1, ECell.Zero);
                // Диагонали
                if (startY + 1 < field.size())
                    field.get(startY+1).set(startX - 1, ECell.Zero);
                if (startY - 1 >= 0)
                    field.get(startY - 1).set(startX - 1, ECell.Zero);
            }

        } else {
            for (int i = startY; i<startY+size; i++){
                field.get(i).set(startX, ECell.Zero);
                // Занимаем клетки левее и правее корабля
                if (startX + 1 < field.size())
                    field.get(i).set(startX + 1, ECell.Zero);
                if (startX - 1 >= 0)
                    field.get(i).set(startX - 1, ECell.Zero);
            }
            if (startY + size < field.size()) {
                field.get(startY + size).set(startX, ECell.Zero);
                // Диагонали
                if (startX + 1 < field.size())
                    field.get(startY + size).set(startX + 1, ECell.Zero);
                if (startX - 1 >= 0)
                    field.get(startY + size).set(startX - 1, ECell.Zero);
            }
            if (startY - 1 >= 0) {
                field.get(startY - 1).set(startX, ECell.Zero);
                // Диагонали
                if (startX + 1 < field.size())
                    field.get(startY - 1).set(startX + 1, ECell.Zero);
                if (startX - 1 >= 0)
                    field.get(startY - 1).set(startX - 1, ECell.Zero);
            }
        }
    }

    // Выстрел в корабль
    public void getHit() {
        setActualSize(actualSize-1);
    }
}
