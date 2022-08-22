package seaBattleDraw;

import seaBattleComponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

class Cell {
    public int x = 0;
    public int y = 0;
}

public class SeaBattleDraw extends JComponent {

    // Размер поля
    private final int size;
    // Отступ поля
    private final int margin;
    private final SeaBattle firstPlayer;
    private final SeaBattle secondPlayer;
    private SeaBattle currentPlayer;
    // Корабль, который игрок сейчас поставит
    private Ship activeShip;
    // Статус игры (расстановка, игра идет, кто-то выиграл)
    private EGameStatus status;
    // Клетка, на которую игрок навелся
    private Cell activeCell;
    private String shotStatus;
    // Размер клетки (пиксели)
    private static final int cellSize = 60;

    public SeaBattleDraw(int size) {
        this.size = size;
        this.margin = 200;
        this.firstPlayer = new SeaBattle(this.size);
        this.secondPlayer = new SeaBattle(this.size);
        this.currentPlayer = this.firstPlayer;
        this.status = EGameStatus.Arrangement;
        this.activeShip = this.currentPlayer.stockShips.get(0);
        this.activeCell = null;
        this.shotStatus = null;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    // Отрисовка клетки
    private void drawCell(Graphics g, ECell cell, int i, int j) {
        g.setColor(Color.BLACK);
        g.drawRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
        switch (cell) {
            case OffTarget:
                g.drawArc(this.margin + j*cellSize + cellSize/2, this.margin + i*cellSize + cellSize/2, 5, 5, 0, 360);
                break;
            case Ship:
                if (this.status != EGameStatus.Arrangement)
                    break;
                g.setColor(Color.BLUE);
                g.fillRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
                break;
            case ShipHit:
                g.setColor(Color.RED);
                g.drawLine(this.margin + j*cellSize + 10, this.margin + i*cellSize + 10, this.margin + j*cellSize + cellSize - 10, this.margin + i*cellSize + cellSize - 10);
                g.drawLine(this.margin + j*cellSize + cellSize - 10, this.margin + i*cellSize + 10, this.margin + j*cellSize + 10, this.margin + i*cellSize + cellSize - 10);
                break;
            case Occupied:
                g.fillArc(this.margin + j*cellSize + cellSize/2, this.margin + i*cellSize + cellSize/2, 5, 5, 0, 360);
                break;
            case Highlight:
                g.setColor(Color.getHSBColor(211, 110, 155));
                g.fillRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
                break;
            default:
                break;
        }
    }

    // Отрисовка целого поля
    private void drawField(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i<this.size; i++) {
            for (int j = 0; j<this.size; j++) {
                drawCell(g, this.currentPlayer.field.get(i).get(j), i, j);
            }
        }
    }

    // Вывод текста на поле
    private void drawText(Graphics g, String text) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Roboto", Font.PLAIN,40));
        g.drawString(text, this.margin, this.margin-50);
    }

    // Вывод результата выстрела на экран
    private void drawShotStatus(Graphics g) {
        if (this.shotStatus != null) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Roboto", Font.PLAIN, 40));
            g.drawString(this.shotStatus, this.margin, this.margin + this.size * cellSize + 50);
        }
    }


    // ! Отрисовка всего компонента
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.status == EGameStatus.FirstPlayerWon) {
            drawText(g, "Первый игрок выиграл!");
            drawField(g);
            return;
        }
        if (this.status == EGameStatus.SecondPlayerWon){
            drawText(g, "Второй игрок выиграл!");
            drawField(g);
            return;
        }
        if (this.status == EGameStatus.Arrangement)
            this.highlightActiveShip(); // Подсвечиваем активный корабль (тот который ставим)
        else
            // Подсвечиваем активную клетку (куда стреляем)
            if (this.activeCell != null)
                highlightActiveCell(g, this.activeCell.y, this.activeCell.x);
        // Выводим поле
        drawField(g);
        // Выводим статус выстрела (если есть)
        drawShotStatus(g);
        if (status == EGameStatus.Arrangement) {
            if (currentPlayer == firstPlayer)
                drawText(g, "Первый игрок расставляет корабли");
            else
                drawText(g, "Второй игрок расставляет корабли");
        } else {
            if (currentPlayer == firstPlayer)
                drawText(g, "Второй игрок ходит");
            else
                drawText(g, "Первый игрок ходит");
        }
    }

    // Очищаем все подсвеченные клетки
    private void clearHighlightCells() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j<this.size; j++) {
                if (this.currentPlayer.field.get(i).get(j) == ECell.Highlight) {
                    this.currentPlayer.field.get(i).set(j, ECell.Zero);
                }
            }
        }
    }

    // Подсвечиваем клетку, куда стреляем
    private void highlightActiveCell(Graphics g, int i, int j) {
        g.setColor(Color.BLACK);
        g.drawRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
        g.setColor(Color.getHSBColor(211, 110, 155));
        g.fillRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
        g.setColor(Color.BLACK);
        g.drawRect(this.margin + j*cellSize, this.margin + i*cellSize, cellSize, cellSize);
    }


    // Подсвечиваем корабль, который ставим
    private void highlightActiveShip() {
        if (this.activeShip.canPutShip(this.currentPlayer.field)) {
            int i = this.activeShip.getStartY();
            int j = this.activeShip.getStartX();
            if (this.activeShip.getDirection() == EDirection.Right) {
                if (this.activeShip.getSize() + j <= this.size) {
                    this.clearHighlightCells();
                    for (int k = j; k < this.activeShip.getSize() + j; k++) {
                        this.currentPlayer.field.get(i).set(k, ECell.Highlight);
                    }
                }
            } else {
                if (this.activeShip.getSize() + i <= this.size) {
                    this.clearHighlightCells();
                    for (int k = i; k < this.activeShip.getSize() + i; k++) {
                        this.currentPlayer.field.get(k).set(j, ECell.Highlight);
                    }
                }
            }
        }
    }

    // Меняем текущего игрока (поле, которое видим)
    private void changeCurrentPlayer() {
        this.activeCell = null;
        if (this.currentPlayer == this.firstPlayer)
            this.currentPlayer = this.secondPlayer;
        else
            this.currentPlayer = this.firstPlayer;
    }

    // Очищаем все занятые клетки
    private void clearOccupiedCells() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.firstPlayer.field.get(i).get(j) == ECell.Occupied)
                    this.firstPlayer.field.get(i).set(j, ECell.Zero);
                if (this.secondPlayer.field.get(i).get(j) == ECell.Occupied)
                    this.secondPlayer.field.get(i).set(j, ECell.Zero);
            }
        }
    }

    // Движение мыши
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (this.status == EGameStatus.FirstPlayerWon || this.status == EGameStatus.SecondPlayerWon)
            return;
        if (e.getID() == 503) {
            int x = e.getX();
            int y = e.getY();
            int i = (int) ((float) (y - margin)/(cellSize));
            int j = (int) ((float) (x - margin)/(cellSize));
            if (i < this.size && i >= 0 && j < this.size && j>=0) {
                if (this.status == EGameStatus.Arrangement) {
                    if (!(this.activeShip.getStartX() == j && this.activeShip.getStartY() == i)) {
                        this.activeShip.setStartX(j);
                        this.activeShip.setStartY(i);
                        repaint();
                    }
                } else {
                    if (this.currentPlayer.field.get(i).get(j) == ECell.Zero || this.currentPlayer.field.get(i).get(j) == ECell.Ship) {
                        this.activeCell = new Cell();
                        this.activeCell.x = j;
                        this.activeCell.y = i;
                        repaint();
                    }
                }
            }
        }
    }

    // Колесико мыши для смены направления корабля
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e) {
        super.processMouseWheelEvent(e);
        if (this.status == EGameStatus.FirstPlayerWon || this.status == EGameStatus.SecondPlayerWon)
            return;
        this.activeShip.changeDirection();
        repaint();
    }

    // Клик мыши
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (this.status == EGameStatus.FirstPlayerWon || this.status == EGameStatus.SecondPlayerWon)
            return;
        if (e.getButton() == MouseEvent.BUTTON1 && e.getID() == 500) {
            int x = e.getX();
            int y = e.getY();
            int i = (int) ((float) (x - margin)/(cellSize));
            int j = (int) ((float) (y - margin)/(cellSize));
            // Проверяем чтобы игрок попадал хоть в какую-то клетку
            if (i < this.size && i >= 0 && j < this.size && j>=0) {
                // Расстановка кораблей
                if (this.status == EGameStatus.Arrangement) {
                    if (this.currentPlayer.addShip(this.activeShip)) {
                        this.currentPlayer.stockShips.remove(0);
                        if (this.currentPlayer.stockShips.size() > 0) {
                            this.activeShip = this.currentPlayer.stockShips.get(0);
                        } else {
                            // Если корабли кончились, меняем игрока
                            this.changeCurrentPlayer();
                            if (this.currentPlayer.stockShips.size() > 0) {
                                this.activeShip = this.currentPlayer.stockShips.get(0);
                            } else {
                                // Если у всех корабли кончились, то начинаем игру
                                this.changeCurrentPlayer();
                                this.activeShip = null;
                                this.status = EGameStatus.GameGoing;
                                this.clearOccupiedCells();
                            }
                        }
                    }
                } else {
                    // Выстрелы
                    if (this.activeCell != null) {
                        ShotStatus shotStatus = this.currentPlayer.takeShot(this.activeCell.x, this.activeCell.y);
                        this.shotStatus = shotStatus.status;
                        if (shotStatus.getHit) {
                            if (this.currentPlayer.ships.size() == 0) {
                                if (currentPlayer == firstPlayer)
                                    this.status = EGameStatus.SecondPlayerWon;
                                else
                                    this.status = EGameStatus.FirstPlayerWon;
                            }
                        } else {
                            // Меняем игрока только если другой не попал
                            this.changeCurrentPlayer();
                        }
                    }
                }
            }
            repaint();
        }
    }
}
