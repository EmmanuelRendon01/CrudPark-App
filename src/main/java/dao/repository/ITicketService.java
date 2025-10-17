package dao.repository;


import model.Estancia;
import model.Operator;

public interface ITicketService {
    void printTicket(Estancia estancia, Operator operator);
}
