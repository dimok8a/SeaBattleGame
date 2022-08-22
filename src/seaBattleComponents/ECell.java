package seaBattleComponents;

public enum ECell {
    Zero, // Пустая клетка
    OffTarget, // выстрел мимо
    Ship, // Корабль
    ShipHit, // корабль подбит
    Occupied, // В клетку нельзя поставить корабль
    Highlight // Подсветка клетки
}
