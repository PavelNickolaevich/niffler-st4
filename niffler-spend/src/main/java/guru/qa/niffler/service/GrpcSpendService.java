package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.grpc.niffler.grpc.*;
import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.SpendEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.data.repository.SpendRepository;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.qa.niffler.utils.ProtobufUtils.convertFromGoogleDate;
import static guru.qa.niffler.utils.ProtobufUtils.convertToGoogleDate;

@GrpcService
public class GrpcSpendService extends NifflerSpendServiceGrpc.NifflerSpendServiceImplBase {

    private final SpendRepository spendRepository;
    private final CategoryRepository categoryRepository;
    private final GrpcCurrencyClient grpcCurrencyClient;

    @Autowired
    public GrpcSpendService(SpendRepository spendRepository, CategoryRepository categoryRepository, GrpcCurrencyClient grpcCurrencyClient) {
        this.spendRepository = spendRepository;
        this.categoryRepository = categoryRepository;
        this.grpcCurrencyClient = grpcCurrencyClient;
    }

    @Override
    @Transactional(readOnly = true)
    public @Nonnull
    void getSpends(SpendRequest request, StreamObserver<SpendsResponse> responseObserver) {

        List<guru.qa.niffler.model.SpendJson> spendJson = getSpendsEntityForUser(request.getUsername(),
                guru.qa.niffler.model.CurrencyValues.valueOf(request.getFilterCurrency().name()),
                convertFromGoogleDate(request.getFrom()),
                convertFromGoogleDate(request.getTo()))
                .map(guru.qa.niffler.model.SpendJson::fromEntity)
                .toList();

        SpendsResponse response = SpendsResponse.newBuilder()
                .addAllSpendJson(spendJson.stream().map(spend -> SpendJson.newBuilder()
                        .setId(spend.id().toString())
                        .setSpendDate(convertToGoogleDate(spend.spendDate()))
                        .setCategory(spend.category())
                        .setCurrency(CurrencyValues.valueOf(spend.currency().name()))
                        .setAmount(spend.amount())
                        .setDescription(spend.description())
                        .setUsername(spend.username())
                        .build()).toList())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public @Nonnull
    void addSpend(AddSpendRequest request, StreamObserver<SpendJson> responseObserver) {
        final String username = request.getSpend().getUsername();
        final String category = request.getSpend().getCategory();

        SpendEntity spendEntity = new SpendEntity();
        spendEntity.setUsername(username);
        spendEntity.setSpendDate(convertFromGoogleDate(request.getSpend().getSpendDate()));
        spendEntity.setCurrency(
                guru.qa.niffler.model.CurrencyValues.valueOf(request.getSpend().getCurrency().name()));
        spendEntity.setDescription(request.getSpend().getDescription());
        spendEntity.setAmount(request.getSpend().getAmount());

        CategoryEntity categoryEntity = categoryRepository.findAllByUsername(username)
                .stream()
                .filter(c -> c.getCategory().equals(category))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Can't find category by given name: " + category));

        spendEntity.setCategory(categoryEntity);
        SpendEntity result = spendRepository.save(spendEntity);

        SpendJson response = SpendJson.newBuilder()
                .setId(result.getId().toString())
                .setSpendDate(convertToGoogleDate(result.getSpendDate()))
                .setCategory(result.getCategory().getCategory())
                .setCurrency(CurrencyValues.valueOf(result.getCurrency().name()))
                .setAmount(result.getAmount())
                .setDescription(result.getDescription())
                .setUsername(result.getUsername())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    @Nonnull
    public void editSpend(EditSpendRequest request, StreamObserver<SpendJson> responseObserver) {
        Optional<SpendEntity> spendById = spendRepository.findById(UUID.fromString(request.getSpend().getId()));

        if (spendById.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can`t find spend by given id: " + request.getSpend().getId());
        } else {
            final String category = request.getSpend().getCategory();
            CategoryEntity categoryEntity = categoryRepository.findAllByUsername(request.getSpend().getUsername())
                    .stream()
                    .filter(c -> c.getCategory().equals(category))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Can`t find category by given name: " + category));

            SpendEntity spendEntity = spendById.get();
            spendEntity.setSpendDate(convertFromGoogleDate(request.getSpend().getSpendDate()));
            spendEntity.setCategory(categoryEntity);
            spendEntity.setAmount(request.getSpend().getAmount());
            spendEntity.setDescription(request.getSpend().getDescription());

            SpendEntity result = spendRepository.save(spendEntity);

            SpendJson response = SpendJson.newBuilder()
                    .setId(result.getId().toString())
                    .setSpendDate(convertToGoogleDate(result.getSpendDate()))
                    .setCategory(result.getCategory().getCategory())
                    .setCurrency(CurrencyValues.valueOf(result.getCurrency().name()))
                    .setAmount(result.getAmount())
                    .setDescription(result.getDescription())
                    .setUsername(result.getUsername())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    @Transactional
    public void deleteSpend(DeleteSpendRequest request, StreamObserver<Empty> responseObserver) {
        spendRepository.deleteByUsernameAndIdIn(request.getUsername(), request.getIdsList().stream().map(UUID::fromString).toList());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public @Nonnull
    void getStatistic(StatisticRequest request, StreamObserver<StatisticResponse> responseObserver) {
        List<SpendEntity> spendEntities = getSpendsEntityForUser(request.getUsername(),
                guru.qa.niffler.model.CurrencyValues.valueOf(request.getFilterCurrency().name()),
                convertFromGoogleDate(request.getDateFrom()),
                convertFromGoogleDate(request.getDateTo()))
                .toList();

        List<guru.qa.niffler.model.StatisticJson> result = new ArrayList<>();

        guru.qa.niffler.model.CurrencyValues[] desiredCurrenciesInResponse = resolveDesiredCurrenciesInStatistic(guru.qa.niffler.model.CurrencyValues.valueOf(request.getFilterCurrency().name()));

        for (guru.qa.niffler.model.CurrencyValues statisticCurrency : desiredCurrenciesInResponse) {
            guru.qa.niffler.model.StatisticJson enriched = calculateStatistic(statisticCurrency,
                    request.getUsername(),
                    guru.qa.niffler.model.CurrencyValues.valueOf(request.getFilterCurrency().name()),
                    spendEntities,
                    convertFromGoogleDate(request.getDateTo()));
            result.add(enriched);
        }

        StatisticResponse statisticResponse = StatisticResponse.newBuilder()
                .addAllStatisticJson(result.stream().map(statisticJson -> StatisticJson.newBuilder()
                        .setDateFrom(convertToGoogleDate(statisticJson.dateFrom()))
                        .setDateTo(convertToGoogleDate(statisticJson.dateTo()))
                        .setCurrency(CurrencyValues.valueOf(statisticJson.currency().name()))
                        .setTotal(statisticJson.total())
                        .setUserDefaultCurrency(CurrencyValues.valueOf(statisticJson.userDefaultCurrency().name()))
                        .setTotalInUserDefaultCurrency(statisticJson.totalInUserDefaultCurrency())
                        .build())
                        .toList())
                .build();

        responseObserver.onNext(statisticResponse);
        responseObserver.onCompleted();
    }


    @Nonnull
    guru.qa.niffler.model.StatisticJson calculateStatistic(@Nonnull guru.qa.niffler.model.CurrencyValues statisticCurrency,
                                                           @Nonnull String username,
                                                           @Nonnull guru.qa.niffler.model.CurrencyValues userCurrency,
                                                           @Nonnull List<SpendEntity> spendEntities,
                                                           @Nullable Date dateTo) {
        guru.qa.niffler.model.StatisticJson statistic = createDefaultStatisticJson(statisticCurrency, userCurrency, dateTo);
        List<SpendEntity> sortedSpends = spendEntities.stream()
                .filter(se -> se.getCurrency() == statisticCurrency)
                .sorted(Comparator.comparing(SpendEntity::getSpendDate))
                .toList();

        statistic = calculateStatistic(statistic, statisticCurrency, userCurrency, sortedSpends);
        Map<String, List<guru.qa.niffler.model.SpendJson>> spendsByCategory = bindSpendsToCategories(sortedSpends);

        List<guru.qa.niffler.model.StatisticByCategoryJson> sbcjResult = new ArrayList<>();
        for (Map.Entry<String, List<guru.qa.niffler.model.SpendJson>> entry : spendsByCategory.entrySet()) {
            double total = entry.getValue().stream()
                    .map(guru.qa.niffler.model.SpendJson::amount)
                    .map(BigDecimal::valueOf)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();

            double totalInUserDefaultCurrency = grpcCurrencyClient.calculate(
                    total,
                    statisticCurrency,
                    userCurrency
            ).doubleValue();

            sbcjResult.add(new guru.qa.niffler.model.StatisticByCategoryJson(
                    entry.getKey(),
                    total,
                    totalInUserDefaultCurrency,
                    entry.getValue()
            ));
        }

        categoryRepository.findAllByUsername(username).stream()
                .filter(c -> !spendsByCategory.containsKey(c.getCategory()))
                .map(c -> new guru.qa.niffler.model.StatisticByCategoryJson(
                        c.getCategory(),
                        0.0,
                        0.0,
                        Collections.emptyList()
                ))
                .forEach(sbcjResult::add);

        sbcjResult.sort(Comparator.comparing(guru.qa.niffler.model.StatisticByCategoryJson::category));
        statistic.categoryStatistics().addAll(sbcjResult);
        return statistic;
    }

    @Nonnull
    Function<SpendEntity, guru.qa.niffler.model.StatisticJson> enrichStatisticDateFromByFirstStreamElement(@Nonnull guru.qa.niffler.model.StatisticJson statistic) {
        return se -> {
            if (statistic.dateFrom() == null) {
                return new guru.qa.niffler.model.StatisticJson(
                        se.getSpendDate(),
                        statistic.dateTo(),
                        statistic.currency(),
                        statistic.total(),
                        statistic.userDefaultCurrency(),
                        statistic.totalInUserDefaultCurrency(),
                        statistic.categoryStatistics()
                );
            } else {
                return statistic;
            }
        };
    }

    @Nonnull
    Function<SpendEntity, guru.qa.niffler.model.StatisticJson> enrichStatisticTotalAmountByAllStreamElements(@Nonnull guru.qa.niffler.model.StatisticJson statistic) {
        return se -> new guru.qa.niffler.model.StatisticJson(
                statistic.dateFrom(),
                statistic.dateTo(),
                statistic.currency(),
                BigDecimal.valueOf(statistic.total())
                        .add(BigDecimal.valueOf(se.getAmount()))
                        .doubleValue(),
                statistic.userDefaultCurrency(),
                statistic.totalInUserDefaultCurrency(),
                statistic.categoryStatistics()
        );
    }

    @Nonnull
    Function<SpendEntity, guru.qa.niffler.model.StatisticJson> enrichStatisticTotalInUserCurrencyByAllStreamElements(@Nonnull guru.qa.niffler.model.StatisticJson statistic,
                                                                                                                     @Nonnull guru.qa.niffler.model.CurrencyValues statisticCurrency,
                                                                                                                     @Nonnull guru.qa.niffler.model.CurrencyValues userCurrency) {
        return se -> new guru.qa.niffler.model.StatisticJson(
                statistic.dateFrom(),
                statistic.dateTo(),
                statistic.currency(),
                statistic.total(),
                statistic.userDefaultCurrency(),
                (userCurrency != statisticCurrency)
                        ? BigDecimal.valueOf(statistic.totalInUserDefaultCurrency()).add(
                        grpcCurrencyClient.calculate(se.getAmount(), se.getCurrency(), userCurrency)
                ).doubleValue()
                        : statistic.total(),
                statistic.categoryStatistics()
        );
    }

    @Nonnull
    Map<String, List<guru.qa.niffler.model.SpendJson>> bindSpendsToCategories(@Nonnull List<SpendEntity> sortedSpends) {
        return sortedSpends.stream().map(guru.qa.niffler.model.SpendJson::fromEntity)
                .collect(Collectors.groupingBy(
                        guru.qa.niffler.model.SpendJson::category,
                        HashMap::new,
                        Collectors.toCollection(ArrayList::new)
                ));
    }

    @Nonnull
    guru.qa.niffler.model.StatisticJson calculateStatistic(@Nonnull guru.qa.niffler.model.StatisticJson statistic,
                                                           @Nonnull guru.qa.niffler.model.CurrencyValues statisticCurrency,
                                                           @Nonnull guru.qa.niffler.model.CurrencyValues userCurrency,
                                                           @Nonnull List<SpendEntity> sortedSpends) {
        guru.qa.niffler.model.StatisticJson enrichedStatistic = statistic;
        for (SpendEntity spend : sortedSpends) {
            enrichedStatistic =
                    enrichStatisticTotalInUserCurrencyByAllStreamElements(
                            enrichStatisticTotalAmountByAllStreamElements(
                                    enrichStatisticDateFromByFirstStreamElement(
                                            enrichedStatistic
                                    ).apply(spend)
                            ).apply(spend), statisticCurrency, userCurrency)
                            .apply(spend);
        }
        return enrichedStatistic;
    }

    @Nonnull
    guru.qa.niffler.model.StatisticJson createDefaultStatisticJson(@Nonnull guru.qa.niffler.model.CurrencyValues statisticCurrency,
                                                                   @Nonnull guru.qa.niffler.model.CurrencyValues userCurrency,
                                                                   @Nullable Date dateTo) {
        return new guru.qa.niffler.model.StatisticJson(
                null,
                dateTo,
                statisticCurrency,
                0.0,
                userCurrency,
                0.0,
                new ArrayList<>()
        );
    }

    @Nonnull
    guru.qa.niffler.model.CurrencyValues[] resolveDesiredCurrenciesInStatistic(@Nullable guru.qa.niffler.model.CurrencyValues filterCurrency) {
        return filterCurrency != null
                ? new guru.qa.niffler.model.CurrencyValues[]{filterCurrency}
                : guru.qa.niffler.model.CurrencyValues.values();
    }


    private @Nonnull
    Stream<SpendEntity> getSpendsEntityForUser(@Nonnull String username,
                                               @Nullable guru.qa.niffler.model.CurrencyValues filterCurrency,
                                               @Nullable Date dateFrom,
                                               @Nullable Date dateTo) {
        dateTo = dateTo == null
                ? new Date()
                : dateTo;

        List<SpendEntity> spends = dateFrom == null
                ? spendRepository.findAllByUsernameAndSpendDateLessThanEqual(username, dateTo)
                : spendRepository.findAllByUsernameAndSpendDateGreaterThanEqualAndSpendDateLessThanEqual(username, dateFrom, dateTo);

        return spends.stream()
                .filter(se -> filterCurrency == null || se.getCurrency() == filterCurrency);
    }
}
