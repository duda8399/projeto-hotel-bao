package edu.ifmg.com.services;

import edu.ifmg.com.repositories.AccommodationRepository;
import edu.ifmg.com.repositories.ClientRepository;
import edu.ifmg.com.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Transactional
    public String deleteAllData() {
        reservationRepository.deleteAll();
        clientRepository.deleteAll();
        accommodationRepository.deleteAll();

        return "Todos os dados foram deletados com sucesso!";
    }
}
